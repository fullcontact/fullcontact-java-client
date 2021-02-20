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
    CompletableFuture<HttpResponse<String>> responseCF = new CompletableFuture<>();
    HttpRequest httpRequest =
        this.buildHttpRequest(FCConstants.personEnrichUri, gson.toJson(personRequest));
    sendRequest(httpRequest, retryHandler, responseCF);
    return responseCF.thenApply(FullContact::getPersonResponse);
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
    return responseCF.thenApply(FullContact::getCompanyResponse);
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
    return responseCF.thenApply(FullContact::getResolveResponseWithTags);
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

  protected CompletableFuture<ResolveResponse> resolveRequest(
      ResolveRequest resolveRequest, RetryHandler retryHandler, URI resolveUri)
      throws FullContactException {
    checkForShutdown();
    CompletableFuture<HttpResponse<String>> responseCF = new CompletableFuture<>();
    HttpRequest httpRequest = this.buildHttpRequest(resolveUri, gson.toJson(resolveRequest));
    sendRequest(httpRequest, retryHandler, responseCF);
    return responseCF.thenApply(FullContact::getResolveResponse);
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
    return responseCF.thenApply(FullContact::getEmailVerificationResponse);
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
    return responseCF.thenApply(FullContact::getTagsResponse);
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
    return responseCF.thenApply(FullContact::getTagsResponse);
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
    return responseCF.thenApply(FullContact::getTagsResponse);
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
    return responseCF.thenApply(FullContact::getAudienceResponse);
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
   * This method creates person enrich response and handle for different response codes
   *
   * @param httpResponse raw response from person enrich API
   * @return PersonResponse
   */
  protected static PersonResponse getPersonResponse(HttpResponse<String> httpResponse) {
    PersonResponse personResponse;
    if (httpResponse.body() != null && !httpResponse.body().trim().isEmpty()) {
      personResponse = gson.fromJson(httpResponse.body(), PersonResponse.class);
      if (httpResponse.statusCode() == 200) {
        personResponse.message = FCConstants.HTTP_RESPONSE_STATUS_200_MESSAGE;
      }
    } else {
      personResponse = new PersonResponse();
      if (httpResponse.statusCode() >= 500) {
        personResponse.message = FCConstants.HTTP_RESPONSE_STATUS_50X_MESSAGE;
      }
    }
    personResponse.isSuccessful =
        (httpResponse.statusCode() == 200)
            || (httpResponse.statusCode() == 202)
            || (httpResponse.statusCode() == 404);
    personResponse.statusCode = httpResponse.statusCode();
    return personResponse;
  }

  /**
   * This method creates company enrich response and handle for different response codes
   *
   * @param httpResponse raw response from company enrich API
   * @return CompanyResponse
   */
  protected static CompanyResponse getCompanyResponse(HttpResponse<String> httpResponse) {
    CompanyResponse companyResponse;
    if (httpResponse.body() != null & !httpResponse.body().trim().isEmpty()) {
      companyResponse = gson.fromJson(httpResponse.body(), CompanyResponse.class);
      if (httpResponse.statusCode() == 200) {
        companyResponse.message = FCConstants.HTTP_RESPONSE_STATUS_200_MESSAGE;
      }
    } else {
      companyResponse = new CompanyResponse();
      if (httpResponse.statusCode() >= 500) {
        companyResponse.message = FCConstants.HTTP_RESPONSE_STATUS_50X_MESSAGE;
      }
    }
    companyResponse.isSuccessful =
        (httpResponse.statusCode() == 200)
            || (httpResponse.statusCode() == 202)
            || (httpResponse.statusCode() == 404);
    companyResponse.statusCode = httpResponse.statusCode();
    return companyResponse;
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
   * This method create Resolve response and handle for different response codes
   *
   * @param httpResponse raw response from Resolve APIs
   * @return ResolveResponse
   */
  protected static ResolveResponse getResolveResponse(HttpResponse<String> httpResponse) {
    ResolveResponse resolveResponse;
    if (httpResponse.body() != null && !httpResponse.body().isBlank()) {
      resolveResponse = gson.fromJson(httpResponse.body(), ResolveResponse.class);
      if (httpResponse.statusCode() == 200 || httpResponse.statusCode() == 204) {
        resolveResponse.message = FCConstants.HTTP_RESPONSE_STATUS_200_MESSAGE;
      }
    } else {
      resolveResponse = new ResolveResponse();
      if (httpResponse.statusCode() >= 500) {
        resolveResponse.message = FCConstants.HTTP_RESPONSE_STATUS_50X_MESSAGE;
      }
    }
    resolveResponse.statusCode = httpResponse.statusCode();
    resolveResponse.isSuccessful =
        (httpResponse.statusCode() == 200)
            || (httpResponse.statusCode() == 204)
            || (httpResponse.statusCode() == 404);
    return resolveResponse;
  }

  /**
   * This method create Resolve response with tags and handle for different response codes
   *
   * @param httpResponse raw response from Resolve APIs
   * @return ResolveResponseWithTags
   */
  protected static ResolveResponseWithTags getResolveResponseWithTags(
      HttpResponse<String> httpResponse) {
    ResolveResponseWithTags resolveResponseWithTags;
    if (httpResponse.body() != null && !httpResponse.body().isBlank()) {
      resolveResponseWithTags = gson.fromJson(httpResponse.body(), ResolveResponseWithTags.class);
      if (httpResponse.statusCode() == 200 || httpResponse.statusCode() == 204) {
        resolveResponseWithTags.message = FCConstants.HTTP_RESPONSE_STATUS_200_MESSAGE;
      }
    } else {
      resolveResponseWithTags = new ResolveResponseWithTags();
      if (httpResponse.statusCode() >= 500) {
        resolveResponseWithTags.message = FCConstants.HTTP_RESPONSE_STATUS_50X_MESSAGE;
      }
    }
    resolveResponseWithTags.statusCode = httpResponse.statusCode();
    resolveResponseWithTags.isSuccessful =
        (httpResponse.statusCode() == 200)
            || (httpResponse.statusCode() == 204)
            || (httpResponse.statusCode() == 404);
    return resolveResponseWithTags;
  }

  protected static EmailVerificationResponse getEmailVerificationResponse(
      HttpResponse<String> httpResponse) {
    EmailVerificationResponse emailVerificationResponse;
    if (httpResponse.body() != null && !httpResponse.body().trim().isEmpty()) {
      emailVerificationResponse =
          gson.fromJson(httpResponse.body(), EmailVerificationResponse.class);
      if (httpResponse.statusCode() == 200) {
        emailVerificationResponse.message = FCConstants.HTTP_RESPONSE_STATUS_200_MESSAGE;
      }
    } else {
      emailVerificationResponse = new EmailVerificationResponse();
      if (httpResponse.statusCode() >= 500) {
        emailVerificationResponse.message = FCConstants.HTTP_RESPONSE_STATUS_50X_MESSAGE;
      }
    }
    emailVerificationResponse.isSuccessful =
        (httpResponse.statusCode() == 200)
            || (httpResponse.statusCode() == 202)
            || (httpResponse.statusCode() == 404);
    emailVerificationResponse.statusCode = httpResponse.statusCode();
    return emailVerificationResponse;
  }

  /**
   * This method create Tags response and handle for different response codes
   *
   * @param httpResponse raw response from Tags APIs
   * @return TagsResponse
   */
  protected static TagsResponse getTagsResponse(HttpResponse<String> httpResponse) {
    TagsResponse tagsResponse;
    if (httpResponse.body() != null && !httpResponse.body().isBlank()) {
      tagsResponse = gson.fromJson(httpResponse.body(), TagsResponse.class);
      if (httpResponse.statusCode() == 200 || httpResponse.statusCode() == 204) {
        tagsResponse.message = FCConstants.HTTP_RESPONSE_STATUS_200_MESSAGE;
      }
    } else {
      tagsResponse = new TagsResponse();
      if (httpResponse.statusCode() >= 500) {
        tagsResponse.message = FCConstants.HTTP_RESPONSE_STATUS_50X_MESSAGE;
      }
    }
    tagsResponse.statusCode = httpResponse.statusCode();
    tagsResponse.isSuccessful =
        (httpResponse.statusCode() == 200)
            || (httpResponse.statusCode() == 204)
            || (httpResponse.statusCode() == 404);
    return tagsResponse;
  }

  /**
   * This method creates Audience response and handle for different response codes
   *
   * @param httpResponse raw response from Audience create API
   * @return AudienceResponse
   */
  protected static AudienceResponse getAudienceResponse(HttpResponse<String> httpResponse) {
    AudienceResponse audienceResponse;
    if (httpResponse.body() != null && !httpResponse.body().isBlank()) {
      audienceResponse = gson.fromJson(httpResponse.body(), AudienceResponse.class);
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
