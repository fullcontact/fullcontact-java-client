package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.FCConstants;
import com.fullcontact.apilib.FullContactApi;
import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.auth.CredentialsProvider;
import com.fullcontact.apilib.auth.DefaultCredentialProvider;
import com.fullcontact.apilib.models.Request.*;
import com.fullcontact.apilib.models.Response.*;
import com.fullcontact.apilib.models.enums.FCApiEndpoint;
import com.fullcontact.apilib.retry.DefaultRetryHandler;
import com.fullcontact.apilib.retry.RetryHandler;
import com.fullcontact.apilib.test.MockInterceptor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Builder;
import lombok.SneakyThrows;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The FullContact class represents FullContact client. It supports V3 Person Enrich, Company Enrich
 * and Resolve endpoints. It uses Retrofit for sending all requests. All requests are converted to
 * JSON and sent via POST method asynchronously
 */
public class FullContact implements AutoCloseable {
  private final String baseUrl = FCConstants.API_BASE_DEFAULT;
  private final OkHttpClient httpClient;
  private final FullContactApi client;
  private final CredentialsProvider credentialsProvider;
  private final RetryHandler retryHandler;
  private final Map<String, String> headers;
  private final long connectTimeoutMillis;
  private final ScheduledExecutorService executor;
  private boolean isShutdown = false;
  private static final MediaType JSONMediaType = MediaType.parse("application/json; charset=utf-8");
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
    this.headers = headers != null ? Collections.unmodifiableMap(headers) : null;
    this.connectTimeoutMillis = connectTimeoutMillis > 0 ? connectTimeoutMillis : 3000;
    this.httpClient = this.configureHTTPClientBuilder().build();
    this.client = this.configureRetrofit().create(FullContactApi.class);
    this.executor = new ScheduledThreadPoolExecutor(5);
  }
  /**
   * Method to build and create OkHttpClient. All the custom headers and auth key is added here.
   *
   * @return OkHttpClient Builder
   */
  protected OkHttpClient.Builder configureHTTPClientBuilder() {
    OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
    httpClientBuilder.addInterceptor(
        chain -> {
          Request.Builder requestBuilder = chain.request().newBuilder();
          requestBuilder.addHeader("Authorization", "Bearer " + credentialsProvider.getApiKey());
          requestBuilder.addHeader("Content-Type", "application/json");
          requestBuilder.addHeader("User-Agent", FCConstants.USER_AGENT_Java8);
          if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
              if (!entry.getKey().equalsIgnoreCase("authorization")
                  && !entry.getKey().equalsIgnoreCase("Content-Type")
                  && !entry.getKey().equalsIgnoreCase("User-Agent")
                  && entry.getValue() != null) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
              }
            }
          }
          Request request = requestBuilder.build();
          return chain.proceed(request);
        });
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    logging.setLevel(HttpLoggingInterceptor.Level.NONE);
    if (System.getProperty("FC_TEST_ENV", "").equals("FC_TEST")) {
      httpClientBuilder.addInterceptor(new MockInterceptor());
    }
    httpClientBuilder.addInterceptor(logging);
    httpClientBuilder.addNetworkInterceptor(new FCOkHttpInterceptor());
    httpClientBuilder.connectTimeout(this.connectTimeoutMillis, TimeUnit.MILLISECONDS);

    return httpClientBuilder;
  }
  /**
   * Method to create Retrofit client using httpClient and Gson converter.
   *
   * @return Retrofit client
   */
  protected Retrofit configureRetrofit() {
    return new Retrofit.Builder().baseUrl(this.baseUrl).client(this.httpClient).build();
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
    CompletableFuture<Response<ResponseBody>> responseCF = new CompletableFuture<>();
    RequestBody httpRequest = buildHttpRequest(gson.toJson(personRequest));
    CompletableFuture<Response<ResponseBody>> httpResponseCompletableFuture =
        this.client.personEnrich(httpRequest);
    handleHttpResponse(
        httpRequest,
        retryHandler,
        httpResponseCompletableFuture,
        responseCF,
        FCApiEndpoint.PERSON_ENRICH);
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
   * @throws FullContactException exception if client is shutdown or request validation
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
   * @throws FullContactException exception if client is shutdown or request validation
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<CompanyResponse> enrich(
      CompanyRequest companyRequest, RetryHandler retryHandler) throws FullContactException {
    checkForShutdown();
    CompletableFuture<Response<ResponseBody>> responseCF = new CompletableFuture<>();
    RequestBody httpRequest = buildHttpRequest(gson.toJson(companyRequest));
    CompletableFuture<Response<ResponseBody>> httpResponseCompletableFuture =
        this.client.companyEnrich(httpRequest);
    handleHttpResponse(
        httpRequest,
        retryHandler,
        httpResponseCompletableFuture,
        responseCF,
        FCApiEndpoint.COMPANY_ENRICH);
    return responseCF.thenApply(
        httpResponse ->
            (CompanyResponse) FullContact.getFCResponse(httpResponse, CompanyResponse.class));
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
    return resolveRequest(resolveRequest, retryHandler, FCApiEndpoint.IDENTITY_MAP);
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
    return resolveRequest(resolveRequest, retryHandler, FCApiEndpoint.IDENTITY_RESOLVE);
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
    checkForShutdown();
    resolveRequest.validateForIdentityResolve();
    CompletableFuture<Response<ResponseBody>> responseCF = new CompletableFuture<>();
    RequestBody httpRequest = buildHttpRequest(gson.toJson(resolveRequest));
    CompletableFuture<Response<ResponseBody>> httpResponseCompletableFuture =
        this.client.identityResolveWithTags(true, httpRequest);
    handleHttpResponse(
        httpRequest,
        retryHandler,
        httpResponseCompletableFuture,
        responseCF,
        FCApiEndpoint.IDENTITY_RESOLVE_WITH_TAGS);
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
    return resolveRequest(resolveRequest, retryHandler, FCApiEndpoint.IDENTITY_DELETE);
  }

  /**
   * Method for mapping and resolving a record in a single call. It calls 'identity.mapResolve'
   * endpoint. It converts the request to json, send the Asynchronous request using HTTP POST
   * method. It also handles retries based on retryHandler specified at FullContact Client level.
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
   * endpoint. It converts the request to json, send the Asynchronous request using HTTP POST
   * method. It also handles retries based on retryHandler specified.
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
    return resolveRequest(resolveRequest, retryHandler, FCApiEndpoint.IDENTITY_MAP_RESOLVE);
  }

  protected CompletableFuture<ResolveResponse> resolveRequest(
      ResolveRequest resolveRequest, RetryHandler retryHandler, FCApiEndpoint fcApiEndpoint)
      throws FullContactException {
    checkForShutdown();
    CompletableFuture<Response<ResponseBody>> responseCF = new CompletableFuture<>();
    RequestBody httpRequest = buildHttpRequest(gson.toJson(resolveRequest));
    CompletableFuture<Response<ResponseBody>> httpResponseCompletableFuture;
    switch (fcApiEndpoint) {
      case IDENTITY_MAP:
        httpResponseCompletableFuture = this.client.identityMap(httpRequest);
        break;
      case IDENTITY_RESOLVE:
        httpResponseCompletableFuture = this.client.identityResolve(httpRequest);
        break;
      case IDENTITY_DELETE:
        httpResponseCompletableFuture = this.client.identityDelete(httpRequest);
        break;
      case IDENTITY_MAP_RESOLVE:
        httpResponseCompletableFuture = this.client.identityMapResolve(httpRequest);
        break;
      default:
        throw new FullContactException("Wrong API Endpoint provided for Resolve");
    }
    handleHttpResponse(
        httpRequest, retryHandler, httpResponseCompletableFuture, responseCF, fcApiEndpoint);
    return responseCF.thenApply(
        httpResponse ->
            (ResolveResponse) FullContact.getFCResponse(httpResponse, ResolveResponse.class));
  }

  /**
   * Method for adding/creating tags for any recordID in your PIC without any custom RetryHandler,
   * It converts the request to json, send the Asynchronous request using HTTP POST method. It also
   * handles retries based on retryHandler specified at FullContact Client level.
   *
   * @param tagsRequest original request sent by client
   * @return completed CompletableFuture with TagsResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<TagsResponse> tagsCreate(TagsRequest tagsRequest)
      throws FullContactException {
    return this.tagsCreate(tagsRequest, this.retryHandler);
  }

  /**
   * Method for adding/creating tags for any recordID in your PIC. It converts the request to json,
   * send the Asynchronous request using HTTP POST method. It also handles retries based on retry
   * condition.
   *
   * @param tagsRequest original request sent by client
   * @return completed CompletableFuture with TagsResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<TagsResponse> tagsCreate(
      TagsRequest tagsRequest, RetryHandler retryHandler) throws FullContactException {
    checkForShutdown();
    CompletableFuture<Response<ResponseBody>> responseCF = new CompletableFuture<>();
    RequestBody httpRequest = buildHttpRequest(gson.toJson(tagsRequest));
    CompletableFuture<Response<ResponseBody>> httpResponseCompletableFuture =
        this.client.tagsCreate(httpRequest);
    handleHttpResponse(
        httpRequest,
        retryHandler,
        httpResponseCompletableFuture,
        responseCF,
        FCApiEndpoint.TAGS_CREATE);
    return responseCF.thenApply(
        httpResponse -> (TagsResponse) FullContact.getFCResponse(httpResponse, TagsResponse.class));
  }

  /**
   * Method for getting all tags for any recordID in your PIC without any custom RetryHandler, It
   * converts the request to json, send the Asynchronous request using HTTP POST method. It also
   * handles retries based on retryHandler specified at FullContact Client level.
   *
   * @param recordId sent by client
   * @return completed CompletableFuture with TagsResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<TagsResponse> tagsGet(String recordId) throws FullContactException {
    return this.tagsGet(recordId, this.retryHandler);
  }

  /**
   * Method for getting all tags for any recordID in your PIC. It converts the request to json, send
   * the Asynchronous request using HTTP POST method. It also handles retries based on retry
   * condition.
   *
   * @param recordId sent by client
   * @return completed CompletableFuture with TagsResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<TagsResponse> tagsGet(String recordId, RetryHandler retryHandler)
      throws FullContactException {
    checkForShutdown();
    CompletableFuture<Response<ResponseBody>> responseCF = new CompletableFuture<>();
    RequestBody httpRequest = buildHttpRequest("{\"recordId\":\"" + recordId + "\"}");
    CompletableFuture<Response<ResponseBody>> httpResponseCompletableFuture =
        this.client.tagsGet(httpRequest);
    handleHttpResponse(
        httpRequest,
        retryHandler,
        httpResponseCompletableFuture,
        responseCF,
        FCApiEndpoint.TAGS_GET);
    return responseCF.thenApply(
        httpResponse -> (TagsResponse) FullContact.getFCResponse(httpResponse, TagsResponse.class));
  }

  /**
   * Method for deleting tags for any recordID in your PIC without any custom RetryHandler, It
   * converts the request to json, send the Asynchronous request using HTTP POST method. It also
   * handles retries based on retryHandler specified at FullContact Client level.
   *
   * @param tagsRequest original request sent by client
   * @return completed CompletableFuture with TagsResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<TagsResponse> tagsDelete(TagsRequest tagsRequest)
      throws FullContactException {
    return this.tagsDelete(tagsRequest, this.retryHandler);
  }

  /**
   * Method for deleting tags for any recordID in your PIC. It converts the request to json, send
   * the Asynchronous request using HTTP POST method. It also handles retries based on retry
   * condition.
   *
   * @param tagsRequest original request sent by client
   * @return completed CompletableFuture with PersonResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<TagsResponse> tagsDelete(
      TagsRequest tagsRequest, RetryHandler retryHandler) throws FullContactException {
    checkForShutdown();
    CompletableFuture<Response<ResponseBody>> responseCF = new CompletableFuture<>();
    RequestBody httpRequest = buildHttpRequest(gson.toJson(tagsRequest));
    CompletableFuture<Response<ResponseBody>> httpResponseCompletableFuture =
        this.client.tagsDelete(httpRequest);
    handleHttpResponse(
        httpRequest,
        retryHandler,
        httpResponseCompletableFuture,
        responseCF,
        FCApiEndpoint.TAGS_DELETE);
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
   * @return completed CompletableFuture with AudienceResponse
   * @throws FullContactException exception if client is shutdown
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
   * using HTTP POST method. It also handles retries based on retry condition.
   *
   * @param audienceRequest original request sent by client
   * @return completed CompletableFuture with AudienceResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<AudienceResponse> audienceCreate(
      AudienceRequest audienceRequest, RetryHandler retryHandler) throws FullContactException {
    checkForShutdown();
    CompletableFuture<Response<ResponseBody>> responseCF = new CompletableFuture<>();
    RequestBody httpRequest = buildHttpRequest(gson.toJson(audienceRequest));
    CompletableFuture<Response<ResponseBody>> httpResponseCompletableFuture =
        this.client.audienceCreate(httpRequest);
    handleHttpResponse(
        httpRequest,
        retryHandler,
        httpResponseCompletableFuture,
        responseCF,
        FCApiEndpoint.AUDIENCE_CREATE);
    return responseCF.thenApply(FullContact::getAudienceResponse);
  }

  public CompletableFuture<AudienceResponse> audienceDownload(String requestId)
      throws FullContactException {
    checkForShutdown();
    if (requestId != null && !requestId.trim().isEmpty()) {
      CompletableFuture<Response<ResponseBody>> responseCF = new CompletableFuture<>();
      RequestBody httpRequest = buildHttpRequest(requestId);
      CompletableFuture<Response<ResponseBody>> httpResponseCompletableFuture =
          this.client.audienceDownload(requestId);
      handleHttpResponse(
          httpRequest,
          retryHandler,
          httpResponseCompletableFuture,
          responseCF,
          FCApiEndpoint.AUDIENCE_DOWNLOAD);
      return responseCF.thenApply(FullContact::getAudienceResponse);
    } else {
      throw new FullContactException("Email can't be empty");
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
    CompletableFuture<Response<ResponseBody>> responseCF = new CompletableFuture<>();
    RequestBody httpRequest = buildHttpRequest(gson.toJson(permissionRequest));
    CompletableFuture<Response<ResponseBody>> httpResponseCompletableFuture =
        this.client.permissionCreate(httpRequest);
    handleHttpResponse(
        httpRequest,
        retryHandler,
        httpResponseCompletableFuture,
        responseCF,
        FCApiEndpoint.PERMISSION_CREATE);
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
    CompletableFuture<Response<ResponseBody>> responseCF = new CompletableFuture<>();
    RequestBody httpRequest = buildHttpRequest(gson.toJson(multifieldRequest));
    CompletableFuture<Response<ResponseBody>> httpResponseCompletableFuture =
        this.client.permissionDelete(httpRequest);
    handleHttpResponse(
        httpRequest,
        retryHandler,
        httpResponseCompletableFuture,
        responseCF,
        FCApiEndpoint.PERMISSION_DELETE);
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
    CompletableFuture<Response<ResponseBody>> responseCF = new CompletableFuture<>();
    RequestBody httpRequest = buildHttpRequest(gson.toJson(multifieldRequest));
    CompletableFuture<Response<ResponseBody>> httpResponseCompletableFuture =
        this.client.permissionFind(httpRequest);
    handleHttpResponse(
        httpRequest,
        retryHandler,
        httpResponseCompletableFuture,
        responseCF,
        FCApiEndpoint.PERMISSION_FIND);
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
    CompletableFuture<Response<ResponseBody>> responseCF = new CompletableFuture<>();
    RequestBody httpRequest = buildHttpRequest(gson.toJson(multifieldRequest));
    CompletableFuture<Response<ResponseBody>> httpResponseCompletableFuture =
        this.client.permissionCurrent(httpRequest);
    handleHttpResponse(
        httpRequest,
        retryHandler,
        httpResponseCompletableFuture,
        responseCF,
        FCApiEndpoint.PERMISSION_CURRENT);
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
    CompletableFuture<Response<ResponseBody>> responseCF = new CompletableFuture<>();
    RequestBody httpRequest = buildHttpRequest(gson.toJson(channelPurposeRequest));
    CompletableFuture<Response<ResponseBody>> httpResponseCompletableFuture =
        this.client.permissionVerify(httpRequest);
    handleHttpResponse(
        httpRequest,
        retryHandler,
        httpResponseCompletableFuture,
        responseCF,
        FCApiEndpoint.PERMISSION_VERIFY);
    return responseCF.thenApply(
        httpResponse ->
            (ConsentPurposeResponse)
                FullContact.getFCResponse(httpResponse, ConsentPurposeResponse.class));
  }

  // Verify APIs
  /**
   * Method for Verify Signals without any custom RetryHandler, It converts the request to json,
   * send the Asynchronous request using HTTP POST method. It also handles retries based on
   * retryHandler specified at FullContact Client level.
   *
   * @param multifieldRequest original request sent by client
   * @return completed CompletableFuture with SignalsResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<SignalsResponse> verifySignals(MultifieldRequest multifieldRequest)
      throws FullContactException {
    return this.verifySignals(multifieldRequest, this.retryHandler);
  }

  /**
   * Method for Verify Signals. It converts the request to json, send the Asynchronous request using
   * HTTP POST method. It also handles retries based on retry condition.
   *
   * @param multifieldRequest original request sent by client
   * @return completed CompletableFuture with SignalsResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<SignalsResponse> verifySignals(
      MultifieldRequest multifieldRequest, RetryHandler retryHandler) throws FullContactException {
    checkForShutdown();
    multifieldRequest.validate();
    CompletableFuture<Response<ResponseBody>> responseCF = new CompletableFuture<>();
    RequestBody httpRequest = buildHttpRequest(gson.toJson(multifieldRequest));
    CompletableFuture<Response<ResponseBody>> httpResponseCompletableFuture =
        this.client.verifySignals(httpRequest);
    handleHttpResponse(
        httpRequest,
        retryHandler,
        httpResponseCompletableFuture,
        responseCF,
        FCApiEndpoint.VERIFY_SIGNALS);
    return responseCF.thenApply(
        httpResponse ->
            (SignalsResponse) FullContact.getFCResponse(httpResponse, SignalsResponse.class));
  }

  /**
   * Method for Verify Match without any custom RetryHandler, It converts the request to json, send
   * the Asynchronous request using HTTP POST method. It also handles retries based on retryHandler
   * specified at FullContact Client level.
   *
   * @param multifieldRequest original request sent by client
   * @return completed CompletableFuture with MatchResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<MatchResponse> verifyMatch(MultifieldRequest multifieldRequest)
      throws FullContactException {
    return this.verifyMatch(multifieldRequest, this.retryHandler);
  }

  /**
   * Method for Verify Match. It converts the request to json, send the Asynchronous request using
   * HTTP POST method. It also handles retries based on retry condition.
   *
   * @param multifieldRequest original request sent by client
   * @return completed CompletableFuture with MatchResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<MatchResponse> verifyMatch(
      MultifieldRequest multifieldRequest, RetryHandler retryHandler) throws FullContactException {
    checkForShutdown();
    multifieldRequest.validate();
    CompletableFuture<Response<ResponseBody>> responseCF = new CompletableFuture<>();
    RequestBody httpRequest = buildHttpRequest(gson.toJson(multifieldRequest));
    CompletableFuture<Response<ResponseBody>> httpResponseCompletableFuture =
        this.client.verifyMatch(httpRequest);
    handleHttpResponse(
        httpRequest,
        retryHandler,
        httpResponseCompletableFuture,
        responseCF,
        FCApiEndpoint.VERIFY_MATCH);
    return responseCF.thenApply(
        httpResponse ->
            (MatchResponse) FullContact.getFCResponse(httpResponse, MatchResponse.class));
  }

  /**
   * Method for Verify Activity without any custom RetryHandler, It converts the request to json,
   * send the Asynchronous request using HTTP POST method. It also handles retries based on
   * retryHandler specified at FullContact Client level.
   *
   * @param multifieldRequest original request sent by client
   * @return completed CompletableFuture with ActivityResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<ActivityResponse> verifyActivity(MultifieldRequest multifieldRequest)
      throws FullContactException {
    return this.verifyActivity(multifieldRequest, this.retryHandler);
  }

  /**
   * Method for Verify Activity. It converts the request to json, send the Asynchronous request
   * using HTTP POST method. It also handles retries based on retry condition.
   *
   * @param multifieldRequest original request sent by client
   * @return completed CompletableFuture with ActivityResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<ActivityResponse> verifyActivity(
      MultifieldRequest multifieldRequest, RetryHandler retryHandler) throws FullContactException {
    checkForShutdown();
    multifieldRequest.validate();
    CompletableFuture<Response<ResponseBody>> responseCF = new CompletableFuture<>();
    RequestBody httpRequest = buildHttpRequest(gson.toJson(multifieldRequest));
    CompletableFuture<Response<ResponseBody>> httpResponseCompletableFuture =
        this.client.verifyActivity(httpRequest);
    handleHttpResponse(
        httpRequest,
        retryHandler,
        httpResponseCompletableFuture,
        responseCF,
        FCApiEndpoint.VERIFY_ACTIVITY);
    return responseCF.thenApply(
        httpResponse ->
            (ActivityResponse) FullContact.getFCResponse(httpResponse, ActivityResponse.class));
  }

  protected void checkForShutdown() throws FullContactException {
    if (isShutdown) {
      throw new FullContactException("FullContact client is shutdown. Please create a new client");
    }
  }

  protected static RequestBody buildHttpRequest(String request) {
    return RequestBody.create(JSONMediaType, request);
  }

  protected void handleHttpResponse(
      RequestBody httpRequest,
      RetryHandler retryHandler,
      CompletableFuture<Response<ResponseBody>> currentResponse,
      CompletableFuture<Response<ResponseBody>> responseCF,
      FCApiEndpoint fcApiEndpoint) {
    currentResponse.handle(
        (httpResponse, throwable) -> {
          if (throwable != null) {
            handleAutoRetry(
                responseCF, httpResponse, httpRequest, throwable, 0, retryHandler, fcApiEndpoint);
          } else if (httpResponse != null && !retryHandler.shouldRetry(httpResponse.code())) {
            responseCF.complete(httpResponse);
          } else {
            handleAutoRetry(
                responseCF, httpResponse, httpRequest, null, 0, retryHandler, fcApiEndpoint);
          }
          return null;
        });
  }

  /**
   * This method creates person enrich response and handle for different response codes
   *
   * @param response raw response from person enrich API
   * @param fcResponseClass response class to deserialize
   * @return FCResponse
   */
  protected static FCResponse getFCResponse(
      Response<ResponseBody> response, Class<? extends FCResponse> fcResponseClass) {
    FCResponse fcResponse;
    if (response.isSuccessful() && response.body() != null) {
      fcResponse = gson.fromJson(response.body().charStream(), fcResponseClass);
      if (response.code() == 200 || response.code() == 204) {
        fcResponse.message = FCConstants.HTTP_RESPONSE_STATUS_200_MESSAGE;
      }
    } else {
      try {
        fcResponse = fcResponseClass.newInstance();
      } catch (InstantiationException | IllegalAccessException e) {
        fcResponse = new FCResponse();
      }
      if (response.errorBody() != null) {
        fcResponse = gson.fromJson(response.errorBody().charStream(), fcResponseClass);
      } else {
        fcResponse.message = response.message();
      }
    }
    if (fcResponse == null) {
      fcResponse = new FCResponse();
      fcResponse.message = response.message();
    }
    fcResponse.isSuccessful =
        response.code() == 200
            || response.code() == 202
            || response.code() == 204
            || response.code() == 404;
    fcResponse.statusCode = response.code();
    return fcResponse;
  }

  /**
   * This method creates Audience response and handle for different response codes
   *
   * @param response raw response from various tags API
   * @return AudienceResponse
   */
  @SneakyThrows
  protected static AudienceResponse getAudienceResponse(Response<ResponseBody> response) {
    AudienceResponse audienceResponse;
    if (response.isSuccessful() && response.body() != null) {
      String contentType = response.headers().get("Content-Type");
      if (contentType != null && contentType.equals("application/octet-stream")) {
        audienceResponse = new AudienceResponse(response.body().bytes());
      } else {
        audienceResponse = gson.fromJson(response.body().charStream(), AudienceResponse.class);
      }
      if (response.code() == 200) {
        audienceResponse.message = FCConstants.HTTP_RESPONSE_STATUS_200_MESSAGE;
      }
    } else {
      audienceResponse = new AudienceResponse();
      if (response.errorBody() != null) {
        audienceResponse = gson.fromJson(response.errorBody().charStream(), AudienceResponse.class);
      } else {
        audienceResponse.message = response.message();
      }
    }
    audienceResponse.isSuccessful =
        response.code() == 200 || response.code() == 202 || response.code() == 404;
    audienceResponse.statusCode = response.code();
    return audienceResponse;
  }

  /**
   * This method creates permission response and handle for different response codes
   *
   * @param response raw response from PermissionFind API
   * @return PermissionResponseList
   */
  protected static PermissionResponseList getPermissionFindResponse(
      Response<ResponseBody> response) {
    PermissionResponseList permissionResponseList = new PermissionResponseList();
    if (response.body() != null) {
      if (response.code() == 200) {
        permissionResponseList.permissionResponseList =
            gson.fromJson(response.body().charStream(), permissionFindResponseType);
        permissionResponseList.message = FCConstants.HTTP_RESPONSE_STATUS_200_MESSAGE;
        permissionResponseList.isSuccessful = true;
      } else {
        permissionResponseList =
            gson.fromJson(response.body().charStream(), PermissionResponseList.class);
      }
    } else {
      if (response.errorBody() != null) {
        permissionResponseList =
            gson.fromJson(response.errorBody().charStream(), PermissionResponseList.class);
      } else {
        permissionResponseList.message = response.message();
      }
    }
    permissionResponseList.isSuccessful =
        (response.code() == 200) || (response.code() == 202) || (response.code() == 404);
    permissionResponseList.statusCode = response.code();
    return permissionResponseList;
  }

  /**
   * This method creates permission current response and handle for different response codes
   *
   * @param response raw response from PermissionCurrent API
   * @return PermissionCurrentResponseMap
   */
  protected static PermissionCurrentResponseMap getPermissionCurrentResponse(
      Response<ResponseBody> response) {
    PermissionCurrentResponseMap permissionCurrentResponseMap = new PermissionCurrentResponseMap();
    if (response.body() != null) {
      if (response.code() == 200) {
        permissionCurrentResponseMap.responseMap =
            gson.fromJson(response.body().charStream(), permissionCurrentResponseType);
        permissionCurrentResponseMap.message = FCConstants.HTTP_RESPONSE_STATUS_200_MESSAGE;
        permissionCurrentResponseMap.isSuccessful = true;
      } else {
        permissionCurrentResponseMap =
            gson.fromJson(response.body().charStream(), PermissionCurrentResponseMap.class);
      }
    } else {
      if (response.errorBody() != null) {
        permissionCurrentResponseMap =
            gson.fromJson(response.errorBody().charStream(), PermissionCurrentResponseMap.class);
      } else {
        permissionCurrentResponseMap.message = response.message();
      }
    }
    permissionCurrentResponseMap.isSuccessful =
        (response.code() == 200) || (response.code() == 202) || (response.code() == 404);
    permissionCurrentResponseMap.statusCode = response.code();
    return permissionCurrentResponseMap;
  }

  /**
   * This method handles Auto Retry in case retry condition is true. It keeps retrying till the
   * retryAttempts exhaust or the response is successful and completes the responseCF based on
   * result. For retrying, it schedules the request using ScheduledThreadPoolExecutor with
   * retryDelayMillis delay time.
   *
   * @param httpRequest original request by client
   * @param httpResponse response of the last retry, used to complete responseCF if all retry
   *     attempts exhaust
   * @param responseCF result completableFuture which is completed here and returned to client
   * @param throwable exception from last retry, used to completeExceptionally
   *     responseCompletableFutureResult if all retries exhaust
   * @param retryAttemptsDone track the number of retry attempts already done
   * @param retryHandler RetryHandler used for current request
   * @param fcApiEndpoint FullContact API Endpoint for current request
   */
  protected void handleAutoRetry(
      CompletableFuture<Response<ResponseBody>> responseCF,
      Response<ResponseBody> httpResponse,
      RequestBody httpRequest,
      Throwable throwable,
      int retryAttemptsDone,
      RetryHandler retryHandler,
      FCApiEndpoint fcApiEndpoint) {
    if (retryAttemptsDone < (Math.min(retryHandler.getRetryAttempts(), 5))) {
      retryAttemptsDone++;
      int finalRetryAttemptsDone = retryAttemptsDone;
      this.executor.schedule(
          () -> {
            CompletableFuture<Response<ResponseBody>> retryCF =
                getRetryResponseCompletableFuture(httpRequest, fcApiEndpoint);
            retryCF.handle(
                (retryResponse, retryThrowable) -> {
                  if (retryThrowable != null) {
                    handleAutoRetry(
                        responseCF,
                        retryResponse,
                        httpRequest,
                        retryThrowable,
                        finalRetryAttemptsDone,
                        retryHandler,
                        fcApiEndpoint);
                  } else if (retryResponse != null
                      && !retryHandler.shouldRetry(retryResponse.code())) {
                    responseCF.complete(retryResponse);
                  } else {
                    handleAutoRetry(
                        responseCF,
                        retryResponse,
                        httpRequest,
                        null,
                        finalRetryAttemptsDone,
                        retryHandler,
                        fcApiEndpoint);
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

  private CompletableFuture<Response<ResponseBody>> getRetryResponseCompletableFuture(
      RequestBody httpRequest, FCApiEndpoint fcApiEndpoint) {
    CompletableFuture<Response<ResponseBody>> retryCF = new CompletableFuture<>();
    switch (fcApiEndpoint) {
      case PERSON_ENRICH:
        retryCF = this.client.personEnrich(httpRequest);
        break;
      case COMPANY_ENRICH:
        retryCF = this.client.companyEnrich(httpRequest);
        break;
      case IDENTITY_MAP:
        retryCF = this.client.identityMap(httpRequest);
        break;
      case IDENTITY_RESOLVE:
        retryCF = this.client.identityResolve(httpRequest);
        break;
      case IDENTITY_RESOLVE_WITH_TAGS:
        retryCF = this.client.identityResolveWithTags(true, httpRequest);
        break;
      case IDENTITY_DELETE:
        retryCF = this.client.identityDelete(httpRequest);
        break;
      case IDENTITY_MAP_RESOLVE:
        retryCF = this.client.identityMapResolve(httpRequest);
        break;
      case TAGS_CREATE:
        retryCF = this.client.tagsCreate(httpRequest);
        break;
      case TAGS_GET:
        retryCF = this.client.tagsGet(httpRequest);
        break;
      case TAGS_DELETE:
        retryCF = this.client.tagsDelete(httpRequest);
        break;
      case AUDIENCE_CREATE:
        retryCF = this.client.audienceCreate(httpRequest);
        break;
      case AUDIENCE_DOWNLOAD:
        try {
          final Buffer buffer = new Buffer();
          httpRequest.writeTo(buffer);
          retryCF = this.client.audienceDownload(buffer.readUtf8());
        } catch (IOException ignored) {
        }
        break;
      case PERMISSION_CREATE:
        retryCF = this.client.permissionCreate(httpRequest);
        break;
      case PERMISSION_DELETE:
        retryCF = this.client.permissionDelete(httpRequest);
        break;
      case PERMISSION_FIND:
        retryCF = this.client.permissionFind(httpRequest);
        break;
      case PERMISSION_CURRENT:
        retryCF = this.client.permissionCurrent(httpRequest);
        break;
      case PERMISSION_VERIFY:
        retryCF = this.client.permissionVerify(httpRequest);
        break;
      case VERIFY_SIGNALS:
        retryCF = this.client.verifySignals(httpRequest);
        break;
      case VERIFY_MATCH:
        retryCF = this.client.verifyMatch(httpRequest);
        break;
      case VERIFY_ACTIVITY:
        retryCF = this.client.verifyActivity(httpRequest);
        break;
      default:
        throw new IllegalStateException("Unexpected API Endpoint: " + fcApiEndpoint);
    }
    return retryCF;
  }

  /** @return Person Request Builder for Person Enrich request */
  public static PersonRequest.PersonRequestBuilder<?, ?> buildPersonRequest() {
    return PersonRequest.personRequestBuilder();
  }

  /** @return Company Request Builder for Company Enrich requests */
  public static CompanyRequest.CompanyRequestBuilder buildCompanyRequest() {
    return CompanyRequest.builder();
  }

  /** @return Resolve Request Builder for Resolve */
  public static ResolveRequest.ResolveRequestBuilder<?, ?> buildResolveRequest() {
    return ResolveRequest.resolveRequestBuilder();
  }

  /** @return TagsRequest Builder for various tags endpoints */
  public static TagsRequest.TagsRequestBuilder buildTagsRequest() {
    return TagsRequest.builder();
  }

  /** @return AudienceRequest Builder for various tags endpoints */
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

  public static class FullContactBuilder {

    /**
     * Validates the builder for authentication and constructs the FullContact client with all the
     * provided values.
     */
    public FullContact build() throws FullContactException {
      this.validate();
      return new FullContact(credentialsProvider, headers, connectTimeoutMillis, retryHandler);
    }

    private void validate() throws FullContactException {
      if (this.credentialsProvider == null) {
        this.credentialsProvider = new DefaultCredentialProvider();
      }
      if (this.retryHandler == null) {
        this.retryHandler = new DefaultRetryHandler();
      }
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
