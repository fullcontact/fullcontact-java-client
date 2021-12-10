package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.FCConstants;
import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.auth.CredentialsProvider;
import com.fullcontact.apilib.auth.DefaultCredentialProvider;
import com.fullcontact.apilib.models.Request.*;
import com.fullcontact.apilib.models.Response.*;
import com.fullcontact.apilib.retry.DefaultRetryHandler;
import com.fullcontact.apilib.retry.RetryHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Builder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The FullContact class represents FullContact client. It supports V3 Person Enrich, Company
 * Enrich, Company Search and Resolve. It uses in-built java11 HttpClient for sending all requests.
 * All requests are converted to JSON and sent via POST method asynchronously
 */
public class FullContact implements AutoCloseable {
  private final CredentialsProvider credentialsProvider;
  private final RetryHandler retryHandler;
  private final HttpClient httpClient;
  private final String[] headersArray;
  private final Duration timeoutDuration;
  private final ScheduledExecutorService executor;
  private boolean isShutdown = false;
  private static final Type companySearchResponseType =
      new TypeToken<ArrayList<CompanySearchResponse>>() {}.getType();
  private static final Type permissionFindResponseType =
      new TypeToken<ArrayList<PermissionResponse>>() {}.getType();
  private static final Type permissionCurrentResponseType =
      new TypeToken<Map<Integer, Map<String, ConsentPurposeResponse>>>() {}.getType();

  private static final Gson gson = new Gson();

  /**
   * FullContact client constructor used to initialise the client
   *
   * @param credentialsProvider for auth
   * @param headers custom client headers
   * @param connectTimeoutMillis connection timout for all requests
   * @param retryHandler RetryHandler specified for client
   */
  @Builder
  public FullContact(
      CredentialsProvider credentialsProvider,
      Map<String, String> headers,
      long connectTimeoutMillis,
      RetryHandler retryHandler) {
    this.credentialsProvider = credentialsProvider;
    this.retryHandler = retryHandler;
    this.headersArray = processHeader(headers);
    this.timeoutDuration =
        Duration.ofMillis(connectTimeoutMillis > 0 ? connectTimeoutMillis : 3000);
    this.httpClient = configureHttpClient();
    this.executor = new ScheduledThreadPoolExecutor(5);
  }

  /** Method to process custom headers, adding auth key and converting to headers array */
  private String[] processHeader(Map<String, String> customHeaders) {
    Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    if (customHeaders != null && !customHeaders.isEmpty()) {
      headers.putAll(customHeaders);
    }
    headers.put("Authorization", "Bearer " + this.credentialsProvider.getApiKey());
    headers.put("Content-Type", "application/json");
    headers.put("User-Agent", FCConstants.USER_AGENT_Java11);
    return headers.entrySet().stream()
        .filter(entry -> entry.getValue() != null)
        .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList())
        .toArray(String[]::new);
  }

  /** Builds a new HttpClient with specified properties */
  protected HttpClient configureHttpClient() {
    return HttpClient.newBuilder().connectTimeout(this.timeoutDuration).build();
  }

  /** @return Person Request Builder for Person Enrich request */
  public static PersonRequest.PersonRequestBuilder<?, ?> buildPersonRequest() {
    return PersonRequest.personRequestBuilder();
  }

  /** @return Company Request Builder for Company Enrich and Company Search requests */
  public static CompanyRequest.CompanyRequestBuilder buildCompanyRequest() {
    return CompanyRequest.builder();
  }

  /** @return Resolve Request Builder for Resolve */
  public static ResolveRequest.ResolveRequestBuilder<?, ?> buildResolveRequest() {
    return ResolveRequest.resolveRequestBuilder();
  }

  /** @return Tags Request Builder for various Tags APIs */
  public static TagsRequest.TagsRequestBuilder buildTagsRequest() {
    return TagsRequest.builder();
  }

  /** @return Audience Request Builder for creating audience from your PIC */
  public static AudienceRequest.AudienceRequestBuilder buildAudienceRequest() {
    return AudienceRequest.builder();
  }
  /** @return Multifield Request builder */
  public static MultifieldRequest.MultifieldRequestBuilder<?, ?> buildMultifieldRequest() {
    return MultifieldRequest.builder();
  }

  /** @return Permission Request builder for Permission Create and Delete APIs */
  public static PermissionRequest.PermissionRequestBuilder<?, ?> buildPermissionRequest() {
    return PermissionRequest.permissionRequestBuilder();
  }

  /** @return ChannelPurpose Request builder for Permission Verify API */
  public static ChannelPurposeRequest.ChannelPurposeRequestBuilder buildChannelPurposeRequest() {
    return ChannelPurposeRequest.builder();
  }

  /**
   * Method for Person Enrich without any custom RetryHandler, It converts the request to json, send
   * the Asynchronous request using HTTP POST method. It also handles retries based on retryHandler
   * specified at FullContact Client level.
   *
   * @param personRequest original request sent by client
   * @return completed CompletableFuture with PersonResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<PersonResponse> enrich(PersonRequest personRequest)
      throws FullContactException {
    return this.enrich(personRequest, this.retryHandler);
  }

  /**
   * Method for Person Enrich. It converts the request to json, send the Asynchronous request using
   * HTTP POST method. It also handles retries based on retry condition.
   *
   * @param personRequest original request sent by client
   * @return completed CompletableFuture with PersonResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<PersonResponse> enrich(
      PersonRequest personRequest, RetryHandler retryHandler) throws FullContactException {
    checkForShutdown();
    personRequest.validate();
    CompletableFuture<HttpResponse<String>> responseCF = new CompletableFuture<>();
    HttpRequest httpRequest =
        this.buildHttpRequest(FCConstants.personEnrichUri, gson.toJson(personRequest));
    sendRequest(httpRequest, retryHandler, responseCF);
    return responseCF.thenApply(
        httpResponse ->
            (PersonResponse) FullContact.getFCResponse(httpResponse, PersonResponse.class));
  }

  /**
   * Method for Company Enrich without any custom RetryHandler, It converts the request to json,
   * send the Asynchronous request using HTTP POST method. It also handles retries based on
   * retryHandler specified at FullContact Client level.
   *
   * @param companyRequest original request sent by client
   * @return completed CompletableFuture with CompanyResponse
   * @throws FullContactException exception if client is shutdown or request fails validation
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<CompanyResponse> enrich(CompanyRequest companyRequest)
      throws FullContactException {
    return this.enrich(companyRequest, this.retryHandler);
  }

  /**
   * Method for Company Enrich. It converts the request to json, send the Asynchronous request using
   * HTTP POST method. It also handles retries based on retry condition.
   *
   * @param companyRequest original request sent by client
   * @return completed CompletableFuture with CompanyResponse
   * @throws FullContactException exception if client is shutdown or request fails validation
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<CompanyResponse> enrich(
      CompanyRequest companyRequest, RetryHandler retryHandler) throws FullContactException {
    checkForShutdown();
    companyRequest.validateForEnrich();
    CompletableFuture<HttpResponse<String>> responseCF = new CompletableFuture<>();
    HttpRequest httpRequest =
        this.buildHttpRequest(FCConstants.companyEnrichUri, gson.toJson(companyRequest));
    sendRequest(httpRequest, retryHandler, responseCF);
    return responseCF.thenApply(
        httpResponse ->
            (CompanyResponse) FullContact.getFCResponse(httpResponse, CompanyResponse.class));
  }

  /**
   * Method for Company Search without any custom RetryHandler, It converts the request to json,
   * send the Asynchronous request using HTTP POST method. It also handles retries based on
   * retryHandler specified at FullContact Client level.
   *
   * @param companyRequest original request sent by client
   * @return completed CompletableFuture with CompanySearchResponseList
   * @throws FullContactException exception if client is shutdown or request fails validation
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<CompanySearchResponseList> search(CompanyRequest companyRequest)
      throws FullContactException {
    return this.search(companyRequest, this.retryHandler);
  }

  /**
   * Method for Company Search. It converts the request to json, send the Asynchronous request using
   * HTTP POST method. It also handles retries based on retry condition.
   *
   * @param companyRequest original request sent by client
   * @return completed CompletableFuture with CompanySearchResponseList
   * @throws FullContactException exception if client is shutdown or request fails validation
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<CompanySearchResponseList> search(
      CompanyRequest companyRequest, RetryHandler retryHandler) throws FullContactException {
    checkForShutdown();
    companyRequest.validateForSearch();

    CompletableFuture<HttpResponse<String>> responseCF = new CompletableFuture<>();
    HttpRequest httpRequest =
        this.buildHttpRequest(FCConstants.companySearchUri, gson.toJson(companyRequest));
    sendRequest(httpRequest, retryHandler, responseCF);
    return responseCF.thenApply(FullContact::getCompanySearchResponseList);
  }

  /**
   * Method for Resolve Identity Map. It converts the request to json, send the Asynchronous request
   * using HTTP POST method. It also handles retries based on retryHandler specified at FullContact
   * Client level.
   *
   * @param resolveRequest original request sent by client
   * @return completed CompletableFuture with ResolveResponse
   * @throws FullContactException exception if client is shutdown or request fails validation
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<ResolveResponse> identityMap(ResolveRequest resolveRequest)
      throws FullContactException {
    return this.identityMap(resolveRequest, this.retryHandler);
  }

  /**
   * Method for Resolve Identity Map. It converts the request to json, send the Asynchronous request
   * using HTTP POST method. It also handles retries based on retryHandler specified.
   *
   * @param resolveRequest original request sent by client
   * @return completed CompletableFuture with ResolveResponse
   * @throws FullContactException exception if client is shutdown or request fails validation
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<ResolveResponse> identityMap(
      ResolveRequest resolveRequest, RetryHandler retryHandler) throws FullContactException {
    resolveRequest.validateForIdentityMap();
    return resolveRequest(resolveRequest, retryHandler, FCConstants.identityMapUri);
  }

  /**
   * Method for Identity Resolve. It converts the request to json, send the Asynchronous request
   * using HTTP POST method. It also handles retries based on retryHandler specified at FullContact
   * Client level.
   *
   * @param resolveRequest original request sent by client
   * @return completed CompletableFuture with ResolveResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<ResolveResponse> identityResolve(ResolveRequest resolveRequest)
      throws FullContactException {
    return this.identityResolve(resolveRequest, this.retryHandler);
  }

  /**
   * Method for Identity Resolve. It converts the request to json, send the Asynchronous request
   * using HTTP POST method. It also handles retries based on retryHandler specified.
   *
   * @param resolveRequest original request sent by client
   * @return completed CompletableFuture with ResolveResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<ResolveResponse> identityResolve(
      ResolveRequest resolveRequest, RetryHandler retryHandler) throws FullContactException {
    resolveRequest.validateForIdentityResolve();
    return resolveRequest(resolveRequest, retryHandler, FCConstants.identityResolveUri);
  }

  /**
   * Method for Identity Resolve with Tags. It converts the request to json, send the Asynchronous
   * request using HTTP POST method. It also handles retries based on retryHandler specified at
   * FullContact Client level.
   *
   * @param resolveRequest original request sent by client
   * @return completed CompletableFuture with ResolveResponseWithTags
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<ResolveResponseWithTags> identityResolveWithTags(
      ResolveRequest resolveRequest) throws FullContactException {
    return this.identityResolveWithTags(resolveRequest, this.retryHandler);
  }

  /**
   * Method for Identity Resolve with Tags. It converts the request to json, send the Asynchronous
   * request using HTTP POST method. It also handles retries based on retryHandler specified.
   *
   * @param resolveRequest original request sent by client
   * @return completed CompletableFuture with ResolveResponseWithTags
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<ResolveResponseWithTags> identityResolveWithTags(
      ResolveRequest resolveRequest, RetryHandler retryHandler) throws FullContactException {
    resolveRequest.validateForIdentityResolve();
    checkForShutdown();
    CompletableFuture<HttpResponse<String>> responseCF = new CompletableFuture<>();
    HttpRequest httpRequest =
        this.buildHttpRequest(FCConstants.identityResolveUriWithTags, gson.toJson(resolveRequest));
    sendRequest(httpRequest, retryHandler, responseCF);
    return responseCF.thenApply(
        httpResponse ->
            (ResolveResponseWithTags)
                FullContact.getFCResponse(httpResponse, ResolveResponseWithTags.class));
  }

  /**
   * Method for Deleting mapped Record. It calls 'identity.delete' endpoint in Resolve. It converts
   * the request to json, send the Asynchronous request using HTTP POST method. It also handles
   * retries based on retryHandler specified at FullContact Client level.
   *
   * @param resolveRequest original request sent by client
   * @return completed CompletableFuture with ResolveResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<ResolveResponse> identityDelete(ResolveRequest resolveRequest)
      throws FullContactException {
    return this.identityDelete(resolveRequest, this.retryHandler);
  }

  /**
   * Method for Deleting mapped Record. It calls 'identity.delete' endpoint in Resolve. It converts
   * the request to json, send the Asynchronous request using HTTP POST method. It also handles
   * retries based on retryHandler specified.
   *
   * @param resolveRequest original request sent by client
   * @return completed CompletableFuture with ResolveResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<ResolveResponse> identityDelete(
      ResolveRequest resolveRequest, RetryHandler retryHandler) throws FullContactException {
    resolveRequest.validateForIdentityDelete();
    return resolveRequest(resolveRequest, retryHandler, FCConstants.identityDeleteUri);
  }

  /**
   * Method for mapping and resolving a record in a single call. It calls 'identity.mapResolve'
   * endpoint in Resolve. It converts the request to json, send the Asynchronous request using HTTP
   * POST method. It also handles retries based on retryHandler specified at FullContact Client
   * level.
   *
   * @param resolveRequest original request sent by client
   * @return completed CompletableFuture with ResolveResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<ResolveResponse> identityMapResolve(ResolveRequest resolveRequest)
      throws FullContactException {
    return this.identityMapResolve(resolveRequest, this.retryHandler);
  }

  /**
   * Method for mapping and resolving a record in a single call. It calls 'identity.mapResolve'
   * endpoint in Resolve. It converts the request to json, send the Asynchronous request using HTTP
   * POST method. It also handles retries based on retryHandler specified.
   *
   * @param resolveRequest original request sent by client
   * @return completed CompletableFuture with ResolveResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<ResolveResponse> identityMapResolve(
      ResolveRequest resolveRequest, RetryHandler retryHandler) throws FullContactException {
    resolveRequest.validateForIdentityMap();
    return resolveRequest(resolveRequest, retryHandler, FCConstants.identityMapResolveUri);
  }

  protected CompletableFuture<ResolveResponse> resolveRequest(
      ResolveRequest resolveRequest, RetryHandler retryHandler, URI resolveUri)
      throws FullContactException {
    checkForShutdown();
    CompletableFuture<HttpResponse<String>> responseCF = new CompletableFuture<>();
    HttpRequest httpRequest = this.buildHttpRequest(resolveUri, gson.toJson(resolveRequest));
    sendRequest(httpRequest, retryHandler, responseCF);
    return responseCF.thenApply(
        httpResponse ->
            (ResolveResponse) FullContact.getFCResponse(httpResponse, ResolveResponse.class));
  }

  /**
   * Method for Email Verification without any custom RetryHandler, It sends Asynchronous request
   * using HTTP GET method. It also handles retries based on retryHandler specified at FullContact
   * Client level.
   *
   * @param email original request sent by client
   * @return completed CompletableFuture with EmailVerificationResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<EmailVerificationResponse> emailVerification(String email)
      throws FullContactException {
    return this.emailVerification(email, this.retryHandler);
  }

  /**
   * Method for Email Verification. It sends Asynchronous request using HTTP GET method. It also
   * handles retries based on retry condition.
   *
   * @param email original request sent by client
   * @return completed CompletableFuture with EmailVerificationResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<EmailVerificationResponse> emailVerification(
      String email, RetryHandler retryHandler) throws FullContactException {
    checkForShutdown();
    CompletableFuture<HttpResponse<String>> responseCF = new CompletableFuture<>();
    HttpRequest httpRequest =
        this.buildHttpGetRequest(
            URI.create(
                FCConstants.API_BASE_V2
                    + FCConstants.API_ENDPOINT_VERIFICATION_EMAIL
                    + "?email="
                    + email));
    sendRequest(httpRequest, retryHandler, responseCF);
    return responseCF.thenApply(
        httpResponse ->
            (EmailVerificationResponse)
                FullContact.getFCResponse(httpResponse, EmailVerificationResponse.class));
  }

  /**
   * Method for adding/creating tags for any recordID in your PIC. It converts the request to json,
   * send the Asynchronous request using HTTP POST method. It also handles retries based on
   * retryHandler specified at FullContact Client level.
   *
   * @param tagsRequest original request sent by client
   * @return completed CompletableFuture with TagsResponse
   * @throws FullContactException exception if client is shutdown or request fails validation
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<TagsResponse> tagsCreate(TagsRequest tagsRequest)
      throws FullContactException {
    return this.tagsCreate(tagsRequest, this.retryHandler);
  }

  /**
   * Method for adding/creating tags for any recordID in your PIC. It converts the request to json,
   * send the Asynchronous request using HTTP POST method. It also handles retries based on
   * retryHandler specified.
   *
   * @param tagsRequest original request sent by client
   * @return completed CompletableFuture with TagsResponse
   * @throws FullContactException exception if client is shutdown or request fails validation
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<TagsResponse> tagsCreate(
      TagsRequest tagsRequest, RetryHandler retryHandler) throws FullContactException {
    checkForShutdown();
    CompletableFuture<HttpResponse<String>> responseCF = new CompletableFuture<>();
    HttpRequest httpRequest =
        this.buildHttpRequest(FCConstants.tagsCreateUri, gson.toJson(tagsRequest));
    sendRequest(httpRequest, retryHandler, responseCF);
    return responseCF.thenApply(
        httpResponse -> (TagsResponse) FullContact.getFCResponse(httpResponse, TagsResponse.class));
  }

  /**
   * Method for getting all tags for any recordID in your PIC. It converts the request to json, send
   * the Asynchronous request using HTTP POST method. It also handles retries based on retryHandler
   * specified at FullContact Client level.
   *
   * @param recordId sent by client
   * @return completed CompletableFuture with TagsResponse
   * @throws FullContactException exception if client is shutdown or request fails validation
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<TagsResponse> tagsGet(String recordId) throws FullContactException {
    return this.tagsGet(recordId, this.retryHandler);
  }

  /**
   * Method for getting tags for any recordID in your PIC. It converts the request to json, send the
   * Asynchronous request using HTTP POST method. It also handles retries based on retryHandler
   * specified.
   *
   * @param recordId sent by client
   * @return completed CompletableFuture with TagsResponse
   * @throws FullContactException exception if client is shutdown or request fails validation
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<TagsResponse> tagsGet(String recordId, RetryHandler retryHandler)
      throws FullContactException {
    checkForShutdown();
    CompletableFuture<HttpResponse<String>> responseCF = new CompletableFuture<>();
    HttpRequest httpRequest =
        this.buildHttpRequest(FCConstants.tagsGetUri, "{\"recordId\":\"" + recordId + "\"}");
    sendRequest(httpRequest, retryHandler, responseCF);
    return responseCF.thenApply(
        httpResponse -> (TagsResponse) FullContact.getFCResponse(httpResponse, TagsResponse.class));
  }

  /**
   * Method for deleting any tags for any recordID in your PIC. It converts the request to json,
   * send the Asynchronous request using HTTP POST method. It also handles retries based on
   * retryHandler specified at FullContact Client level.
   *
   * @param tagsRequest original request sent by client
   * @return completed CompletableFuture with TagsResponse
   * @throws FullContactException exception if client is shutdown or request fails validation
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<TagsResponse> tagsDelete(TagsRequest tagsRequest)
      throws FullContactException {
    return this.tagsDelete(tagsRequest, this.retryHandler);
  }

  /**
   * Method for deleting any tags for any recordID in your PIC. It converts the request to json,
   * send the Asynchronous request using HTTP POST method. It also handles retries based on
   * retryHandler specified.
   *
   * @param tagsRequest original request sent by client
   * @return completed CompletableFuture with TagsResponse
   * @throws FullContactException exception if client is shutdown or request fails validation
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<TagsResponse> tagsDelete(
      TagsRequest tagsRequest, RetryHandler retryHandler) throws FullContactException {
    checkForShutdown();
    CompletableFuture<HttpResponse<String>> responseCF = new CompletableFuture<>();
    HttpRequest httpRequest =
        this.buildHttpRequest(FCConstants.tagsDeleteUri, gson.toJson(tagsRequest));
    sendRequest(httpRequest, retryHandler, responseCF);
    return responseCF.thenApply(
        httpResponse -> (TagsResponse) FullContact.getFCResponse(httpResponse, TagsResponse.class));
  }

  /**
   * Method for creating Audience from your PIC based on tags. WebhookUrl and at least one tag is
   * mandatory for this request. It converts the request to json, send the Asynchronous request
   * using HTTP POST method. It also handles retries based on retryHandler specified at FullContact
   * Client level.
   *
   * @param audienceRequest original request sent by client
   * @return completed CompletableFuture with with AudienceResponse
   * @throws FullContactException exception if client is shutdown or request fails validation
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<AudienceResponse> audienceCreate(AudienceRequest audienceRequest)
      throws FullContactException {
    return this.audienceCreate(audienceRequest, this.retryHandler);
  }

  /**
   * Method for creating Audience from your PIC based on tags. WebhookUrl and at least one tag is
   * mandatory for this request. It converts the request to json, send the Asynchronous request
   * using HTTP POST method. It also handles retries based on retryHandler specified.
   *
   * @param audienceRequest original request sent by client
   * @return completed CompletableFuture with AudienceResponse
   * @throws FullContactException exception if client is shutdown or request fails validation
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<AudienceResponse> audienceCreate(
      AudienceRequest audienceRequest, RetryHandler retryHandler) throws FullContactException {
    checkForShutdown();
    CompletableFuture<HttpResponse<String>> responseCF = new CompletableFuture<>();
    HttpRequest httpRequest =
        this.buildHttpRequest(FCConstants.audienceCreateUri, gson.toJson(audienceRequest));
    sendRequest(httpRequest, retryHandler, responseCF);
    return responseCF.thenApply(
        httpResponse ->
            (AudienceResponse) FullContact.getFCResponse(httpResponse, AudienceResponse.class));
  }

  /**
   * Method for downloading Audience file using requestId from 'audience.create'.
   *
   * @param requestId original request sent by client
   * @return completed CompletableFuture with AudienceResponse
   * @throws FullContactException exception if client is shutdown or request fails validation
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<AudienceResponse> audienceDownload(String requestId)
      throws FullContactException {
    checkForShutdown();
    if (requestId != null && !requestId.isBlank()) {
      HttpRequest httpRequest =
          this.buildHttpGetRequest(
              URI.create(
                  FCConstants.API_BASE_DEFAULT
                      + FCConstants.API_ENDPOINT_AUDIENCE_DOWNLOAD
                      + "?requestId="
                      + requestId));
      CompletableFuture<HttpResponse<byte[]>> responseCF = new CompletableFuture<>();
      this.httpClient
          .sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray())
          .handle(
              (httpResponse, throwable) -> {
                if (httpResponse != null) {
                  responseCF.complete(httpResponse);
                } else {
                  responseCF.completeExceptionally(throwable);
                }
                return null;
              });
      return responseCF.thenApply(FullContact::getAudienceDownloadResponse);
    } else {
      throw new FullContactException("'requestId' can't be empty");
    }
  }

  /**
   * Method for Permission Create without any custom RetryHandler, It converts the request to json,
   * send the Asynchronous request using HTTP POST method. It also handles retries based on
   * retryHandler specified at FullContact Client level.
   *
   * @param permissionRequest original request sent by client
   * @return completed CompletableFuture with FCResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<FCResponse> permissionCreate(PermissionRequest permissionRequest)
      throws FullContactException {
    return this.permissionCreate(permissionRequest, this.retryHandler);
  }

  /**
   * Method for Permission Create. It converts the request to json, send the Asynchronous request
   * using HTTP POST method. It also handles retries based on retry condition.
   *
   * @param permissionRequest original request sent by client
   * @return completed CompletableFuture with FCResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<FCResponse> permissionCreate(
      PermissionRequest permissionRequest, RetryHandler retryHandler) throws FullContactException {
    checkForShutdown();
    permissionRequest.validate();
    CompletableFuture<HttpResponse<String>> responseCF = new CompletableFuture<>();
    HttpRequest httpRequest =
        this.buildHttpRequest(FCConstants.permissionCreateUri, gson.toJson(permissionRequest));
    sendRequest(httpRequest, retryHandler, responseCF);
    return responseCF.thenApply(
        httpResponse -> FullContact.getFCResponse(httpResponse, FCResponse.class));
  }

  /**
   * Method for Permission Delete without any custom RetryHandler, It converts the request to json,
   * send the Asynchronous request using HTTP POST method. It also handles retries based on
   * retryHandler specified at FullContact Client level.
   *
   * @param multifieldRequest original request sent by client
   * @return completed CompletableFuture with FCResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<FCResponse> permissionDelete(MultifieldRequest multifieldRequest)
      throws FullContactException {
    return this.permissionDelete(multifieldRequest, this.retryHandler);
  }

  /**
   * Method for Permission Delete. It converts the request to json, send the Asynchronous request
   * using HTTP POST method. It also handles retries based on retry condition.
   *
   * @param multifieldRequest original request sent by client
   * @return completed CompletableFuture with FCResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<FCResponse> permissionDelete(
      MultifieldRequest multifieldRequest, RetryHandler retryHandler) throws FullContactException {
    checkForShutdown();
    multifieldRequest.validate();
    CompletableFuture<HttpResponse<String>> responseCF = new CompletableFuture<>();
    HttpRequest httpRequest =
        this.buildHttpRequest(FCConstants.permissionDeleteUri, gson.toJson(multifieldRequest));
    sendRequest(httpRequest, retryHandler, responseCF);
    return responseCF.thenApply(
        httpResponse -> FullContact.getFCResponse(httpResponse, FCResponse.class));
  }

  /**
   * Method for Permission Find without any custom RetryHandler, It converts the request to json,
   * send the Asynchronous request using HTTP POST method. It also handles retries based on
   * retryHandler specified at FullContact Client level.
   *
   * @param multifieldRequest original request sent by client
   * @return completed CompletableFuture with PermissionResponseList
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<PermissionResponseList> permissionFind(
      MultifieldRequest multifieldRequest) throws FullContactException {
    return this.permissionFind(multifieldRequest, this.retryHandler);
  }

  /**
   * Method for Permission Find. It converts the request to json, send the Asynchronous request
   * using HTTP POST method. It also handles retries based on retry condition.
   *
   * @param multifieldRequest original request sent by client
   * @return completed CompletableFuture with PermissionResponseList
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<PermissionResponseList> permissionFind(
      MultifieldRequest multifieldRequest, RetryHandler retryHandler) throws FullContactException {
    checkForShutdown();
    multifieldRequest.validate();
    CompletableFuture<HttpResponse<String>> responseCF = new CompletableFuture<>();
    HttpRequest httpRequest =
        this.buildHttpRequest(FCConstants.permissionFindUri, gson.toJson(multifieldRequest));
    sendRequest(httpRequest, retryHandler, responseCF);
    return responseCF.thenApply(FullContact::getPermissionFindResponse);
  }

  /**
   * Method for Permission Current without any custom RetryHandler, It converts the request to json,
   * send the Asynchronous request using HTTP POST method. It also handles retries based on
   * retryHandler specified at FullContact Client level.
   *
   * @param multifieldRequest original request sent by client
   * @return completed CompletableFuture with PermissionCurrentResponseMap
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<PermissionCurrentResponseMap> permissionCurrent(
      MultifieldRequest multifieldRequest) throws FullContactException {
    return this.permissionCurrent(multifieldRequest, this.retryHandler);
  }

  /**
   * Method for Permission Current. It converts the request to json, send the Asynchronous request
   * using HTTP POST method. It also handles retries based on retry condition.
   *
   * @param multifieldRequest original request sent by client
   * @return completed CompletableFuture with PermissionCurrentResponseMap
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<PermissionCurrentResponseMap> permissionCurrent(
      MultifieldRequest multifieldRequest, RetryHandler retryHandler) throws FullContactException {
    checkForShutdown();
    multifieldRequest.validate();
    CompletableFuture<HttpResponse<String>> responseCF = new CompletableFuture<>();
    HttpRequest httpRequest =
        this.buildHttpRequest(FCConstants.permissionCurrentUri, gson.toJson(multifieldRequest));
    sendRequest(httpRequest, retryHandler, responseCF);
    return responseCF.thenApply(FullContact::getPermissionCurrentResponse);
  }

  /**
   * Method for Permission Verify without any custom RetryHandler, It converts the request to json,
   * send the Asynchronous request using HTTP POST method. It also handles retries based on
   * retryHandler specified at FullContact Client level.
   *
   * @param channelPurposeRequest original request sent by client
   * @return completed CompletableFuture with ConsentPurposeResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<ConsentPurposeResponse> permissionVerify(
      ChannelPurposeRequest channelPurposeRequest) throws FullContactException {
    return this.permissionVerify(channelPurposeRequest, this.retryHandler);
  }

  /**
   * Method for Permission Verify. It converts the request to json, send the Asynchronous request
   * using HTTP POST method. It also handles retries based on retry condition.
   *
   * @param channelPurposeRequest original request sent by client
   * @return completed CompletableFuture with ConsentPurposeResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<ConsentPurposeResponse> permissionVerify(
      ChannelPurposeRequest channelPurposeRequest, RetryHandler retryHandler)
      throws FullContactException {
    checkForShutdown();
    channelPurposeRequest.validate();
    CompletableFuture<HttpResponse<String>> responseCF = new CompletableFuture<>();
    HttpRequest httpRequest =
        this.buildHttpRequest(FCConstants.permissionVerifyUri, gson.toJson(channelPurposeRequest));
    sendRequest(httpRequest, retryHandler, responseCF);
    return responseCF.thenApply(
        httpResponse ->
            (ConsentPurposeResponse)
                FullContact.getFCResponse(httpResponse, ConsentPurposeResponse.class));
  }

  protected void checkForShutdown() throws FullContactException {
    if (isShutdown) {
      throw new FullContactException("FullContact client is shutdown. Please create a new client");
    }
  }

  protected HttpRequest buildHttpRequest(URI uri, String request) {
    return HttpRequest.newBuilder(uri)
        .headers(this.headersArray)
        .timeout(this.timeoutDuration)
        .POST(HttpRequest.BodyPublishers.ofString(request))
        .build();
  }

  protected HttpRequest buildHttpGetRequest(URI uri) {
    return HttpRequest.newBuilder(uri)
        .headers(this.headersArray)
        .timeout(this.timeoutDuration)
        .GET()
        .build();
  }

  protected void sendRequest(
      HttpRequest httpRequest,
      RetryHandler retryHandler,
      CompletableFuture<HttpResponse<String>> responseCF) {
    CompletableFuture<HttpResponse<String>> httpResponseCompletableFuture =
        this.httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());

    httpResponseCompletableFuture.handle(
        (httpResponse, throwable) -> {
          if (throwable != null) {
            handleAutoRetry(responseCF, httpResponse, httpRequest, throwable, 0, retryHandler);
          } else if (httpResponse != null && !retryHandler.shouldRetry(httpResponse.statusCode())) {
            responseCF.complete(httpResponse);
          } else {
            handleAutoRetry(responseCF, httpResponse, httpRequest, null, 0, retryHandler);
          }
          return null;
        });
  }

  /**
   * This method creates fc response and handle for different response codes
   *
   * @param httpResponse raw response from person enrich API
   * @param fcResponseClass response class to deserialize
   * @return FCResponse
   */
  protected static FCResponse getFCResponse(
      HttpResponse<String> httpResponse, Class<? extends FCResponse> fcResponseClass) {
    FCResponse fcResponse;
    if (httpResponse.body() != null && !httpResponse.body().trim().isEmpty()) {
      fcResponse = gson.fromJson(httpResponse.body(), fcResponseClass);
      if (httpResponse.statusCode() == 200 || (httpResponse.statusCode() == 204)) {
        fcResponse.message = FCConstants.HTTP_RESPONSE_STATUS_200_MESSAGE;
      }
    } else {
      try {
        fcResponse = fcResponseClass.getDeclaredConstructor().newInstance();
      } catch (InstantiationException
          | IllegalAccessException
          | NoSuchMethodException
          | InvocationTargetException e) {
        fcResponse = new FCResponse();
      }
      if (httpResponse.statusCode() >= 500) {
        fcResponse.message = FCConstants.HTTP_RESPONSE_STATUS_50X_MESSAGE;
      }
    }
    fcResponse.isSuccessful =
        (httpResponse.statusCode() == 200)
            || (httpResponse.statusCode() == 202)
            || (httpResponse.statusCode() == 204)
            || (httpResponse.statusCode() == 404);
    fcResponse.statusCode = httpResponse.statusCode();
    return fcResponse;
  }

  /**
   * This method creates company search response and handle for different response codes
   *
   * @param httpResponse raw response from company search API
   * @return CompanySearchResponseList
   */
  protected static CompanySearchResponseList getCompanySearchResponseList(
      HttpResponse<String> httpResponse) {
    CompanySearchResponseList companySearchResponseList = new CompanySearchResponseList();
    if (httpResponse.body() != null && !httpResponse.body().trim().isEmpty()) {
      if (httpResponse.statusCode() == 200) {
        companySearchResponseList.companySearchResponses =
            gson.fromJson(httpResponse.body(), companySearchResponseType);
        companySearchResponseList.message = FCConstants.HTTP_RESPONSE_STATUS_200_MESSAGE;
      } else {
        companySearchResponseList =
            gson.fromJson(httpResponse.body(), CompanySearchResponseList.class);
      }
    } else {
      if (httpResponse.statusCode() >= 500) {
        companySearchResponseList.message = FCConstants.HTTP_RESPONSE_STATUS_50X_MESSAGE;
      }
    }
    companySearchResponseList.isSuccessful =
        (httpResponse.statusCode() == 200)
            || (httpResponse.statusCode() == 202)
            || (httpResponse.statusCode() == 404);
    companySearchResponseList.statusCode = httpResponse.statusCode();
    return companySearchResponseList;
  }

  /**
   * This method creates Audience response and handle for different response codes
   *
   * @param httpResponse raw response from Audience Download API
   * @return AudienceResponse
   */
  protected static AudienceResponse getAudienceDownloadResponse(HttpResponse<byte[]> httpResponse) {
    AudienceResponse audienceResponse;
    if (httpResponse.body() != null) {
      audienceResponse = new AudienceResponse(httpResponse.body());
      if (httpResponse.statusCode() == 200 || httpResponse.statusCode() == 202) {
        audienceResponse.message = FCConstants.HTTP_RESPONSE_STATUS_200_MESSAGE;
      }
    } else {
      audienceResponse = new AudienceResponse();
      if (httpResponse.statusCode() >= 500) {
        audienceResponse.message = FCConstants.HTTP_RESPONSE_STATUS_50X_MESSAGE;
      }
    }
    audienceResponse.statusCode = httpResponse.statusCode();
    audienceResponse.isSuccessful =
        (httpResponse.statusCode() == 200)
            || (httpResponse.statusCode() == 202)
            || (httpResponse.statusCode() == 404);
    return audienceResponse;
  }

  /**
   * This method creates permission response and handle for different response codes
   *
   * @param httpResponse raw response from PermissionFind API
   * @return PermissionResponseList
   */
  protected static PermissionResponseList getPermissionFindResponse(
      HttpResponse<String> httpResponse) {
    PermissionResponseList permissionResponseList = new PermissionResponseList();
    if (httpResponse.body() != null && !httpResponse.body().trim().isEmpty()) {
      if (httpResponse.statusCode() == 200) {
        permissionResponseList.permissionResponseList =
            gson.fromJson(httpResponse.body(), permissionFindResponseType);
        permissionResponseList.message = FCConstants.HTTP_RESPONSE_STATUS_200_MESSAGE;
      } else {
        permissionResponseList = gson.fromJson(httpResponse.body(), PermissionResponseList.class);
      }
    } else {
      if (httpResponse.statusCode() >= 500) {
        permissionResponseList.message = FCConstants.HTTP_RESPONSE_STATUS_50X_MESSAGE;
      }
    }
    permissionResponseList.isSuccessful =
        (httpResponse.statusCode() == 200)
            || (httpResponse.statusCode() == 202)
            || (httpResponse.statusCode() == 404);
    permissionResponseList.statusCode = httpResponse.statusCode();
    return permissionResponseList;
  }

  /**
   * This method creates permission current response and handle for different response codes
   *
   * @param httpResponse raw response from PermissionCurrent API
   * @return PermissionCurrentResponseMap
   */
  protected static PermissionCurrentResponseMap getPermissionCurrentResponse(
      HttpResponse<String> httpResponse) {
    PermissionCurrentResponseMap permissionCurrentResponseMap = new PermissionCurrentResponseMap();
    if (httpResponse.body() != null && !httpResponse.body().trim().isEmpty()) {
      if (httpResponse.statusCode() == 200) {
        permissionCurrentResponseMap.responseMap =
            gson.fromJson(httpResponse.body(), permissionCurrentResponseType);
        permissionCurrentResponseMap.message = FCConstants.HTTP_RESPONSE_STATUS_200_MESSAGE;
      } else {
        permissionCurrentResponseMap =
            gson.fromJson(httpResponse.body(), PermissionCurrentResponseMap.class);
      }
    } else {
      if (httpResponse.statusCode() >= 500) {
        permissionCurrentResponseMap.message = FCConstants.HTTP_RESPONSE_STATUS_50X_MESSAGE;
      }
    }
    permissionCurrentResponseMap.isSuccessful =
        (httpResponse.statusCode() == 200)
            || (httpResponse.statusCode() == 202)
            || (httpResponse.statusCode() == 404);
    permissionCurrentResponseMap.statusCode = httpResponse.statusCode();
    return permissionCurrentResponseMap;
  }

  /**
   * This method handles Auto Retry in case retry condition is true. It keeps retrying till the
   * retryAttempts exhaust or the response is successful and completes the responseCF based on
   * result. For retrying, it schedules the request using ScheduledThreadPoolExecutor with
   * retryDelayMillis delay time.
   *
   * @param httpRequest reusing the same httpRequest built in enrich method
   * @param httpResponse response of the last retry, used to complete responseCF if all retry
   *     attempts exhaust
   * @param responseCF resultant completableFuture which is completed here and returned to client
   * @param throwable exception from last retry, used to completeExceptionally
   *     responseCompletableFutureResult if all retries exhaust
   * @param retryAttemptsDone track the number of retry attempts already done
   * @param retryHandler RetryHandler specified for the request
   */
  protected void handleAutoRetry(
      CompletableFuture<HttpResponse<String>> responseCF,
      HttpResponse<String> httpResponse,
      HttpRequest httpRequest,
      Throwable throwable,
      int retryAttemptsDone,
      RetryHandler retryHandler) {
    if (retryAttemptsDone < (Math.min(retryHandler.getRetryAttempts(), 5))) {
      retryAttemptsDone++;
      int finalRetryAttemptsDone = retryAttemptsDone;
      this.executor.schedule(
          () -> {
            CompletableFuture<HttpResponse<String>> retryCompletableFuture =
                this.httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
            retryCompletableFuture.handle(
                (retryHttpResponse, retryThrowable) -> {
                  if (retryThrowable != null) {
                    handleAutoRetry(
                        responseCF,
                        retryHttpResponse,
                        httpRequest,
                        retryThrowable,
                        finalRetryAttemptsDone,
                        retryHandler);
                  } else if (retryHttpResponse != null
                      && !retryHandler.shouldRetry(retryHttpResponse.statusCode())) {
                    responseCF.complete(retryHttpResponse);
                  } else {
                    handleAutoRetry(
                        responseCF,
                        retryHttpResponse,
                        httpRequest,
                        null,
                        finalRetryAttemptsDone,
                        retryHandler);
                  }
                  return null;
                });
          },
          Math.max(retryHandler.getRetryDelayMillis(), 500)
              * (long) Math.pow(2, retryAttemptsDone - 1),
          TimeUnit.MILLISECONDS);
    } else if (throwable != null
        && retryAttemptsDone == (Math.min(retryHandler.getRetryAttempts(), 5))) {
      responseCF.completeExceptionally(throwable);
    } else {
      responseCF.complete(httpResponse);
    }
  }

  /**
   * This method is used to call shutdown on the ScheduledThreadPoolExecutor and close the
   * FullContact client.
   */
  @Override
  public void close() {
    if (!this.executor.isShutdown()) {
      this.executor.shutdown();
      this.isShutdown = true;
    }
  }

  /** This method will be called by GC to close the client. */
  @Override
  public void finalize() {
    this.close();
  }

  /** Builder class for building FullContact client. */
  public static class FullContactBuilder {

    private void validate() throws FullContactException {
      if (this.credentialsProvider == null) {
        this.credentialsProvider = new DefaultCredentialProvider();
      }
      if (this.retryHandler == null) {
        this.retryHandler = new DefaultRetryHandler();
      }
    }

    /**
     * Validates the builder for authentication and constructs the FullContact client with all the
     * provided values.
     *
     * @throws FullContactException if API Key not found
     * @return new FullContact client
     */
    public FullContact build() throws FullContactException {
      this.validate();
      return new FullContact(credentialsProvider, headers, connectTimeoutMillis, retryHandler);
    }

    /**
     * Builder method to provide {@link com.fullcontact.apilib.auth.CredentialsProvider} for
     * authentication.
     *
     * @param credentialsProvider implementation of credentialsProvider for auth
     * @return FullContactBuilder
     */
    public FullContactBuilder credentialsProvider(CredentialsProvider credentialsProvider) {
      this.credentialsProvider = credentialsProvider;
      return this;
    }

    /**
     * Builder method to provide custom Headers, which will be included in all requests
     *
     * @param headers customHeaders provided by client
     * @return FullContactBuilder
     */
    public FullContactBuilder headers(Map<String, String> headers) {
      this.headers = headers;
      return this;
    }

    /**
     * Builder method to provide connection timeout, default value is 3000ms
     *
     * @param connectTimeoutMillis Connection Timeout in milliseconds
     * @return FullContactBuilder
     */
    public FullContactBuilder connectTimeoutMillis(long connectTimeoutMillis) {
      this.connectTimeoutMillis = connectTimeoutMillis;
      return this;
    }

    /**
     * Builder method to provide {@link com.fullcontact.apilib.retry.RetryHandler}
     *
     * @param retryHandler custom RetryHandler
     * @return FullContactBuilder
     */
    public FullContactBuilder retryHandler(RetryHandler retryHandler) {
      this.retryHandler = retryHandler;
      return this;
    }
  }
}
