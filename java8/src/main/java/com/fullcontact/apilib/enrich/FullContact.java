package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.FCConstants;
import com.fullcontact.apilib.FullContactApi;
import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.auth.CredentialsProvider;
import com.fullcontact.apilib.auth.DefaultCredentialProvider;
import com.fullcontact.apilib.models.Request.CompanyRequest;
import com.fullcontact.apilib.models.Request.PersonRequest;
import com.fullcontact.apilib.models.Request.ResolveRequest;
import com.fullcontact.apilib.models.Response.*;
import com.fullcontact.apilib.models.enums.FCApiEndpoint;
import com.fullcontact.apilib.retry.DefaultRetryHandler;
import com.fullcontact.apilib.retry.RetryHandler;
import com.fullcontact.apilib.test.MockInterceptor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Builder;
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
 * The FullContact class represents FullContact client. It supports V3 Person Enrich, Company
 * Enrich, Company Search and Resolve endpoints. It uses Retrofit for sending all requests. All
 * requests are converted to JSON and sent via POST method asynchronously
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
    return responseCF.thenApply(FullContact::getPersonResponse);
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
    CompletableFuture<Response<ResponseBody>> responseCF = new CompletableFuture<>();
    RequestBody httpRequest = buildHttpRequest(gson.toJson(companyRequest));
    CompletableFuture<Response<ResponseBody>> httpResponseCompletableFuture =
        this.client.companySearch(httpRequest);
    handleHttpResponse(
        httpRequest,
        retryHandler,
        httpResponseCompletableFuture,
        responseCF,
        FCApiEndpoint.COMPANY_SEARCH);
    return responseCF.thenApply(FullContact::getCompanySearchResponse);
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
   * Method for Resolve Identity Map. It converts the request to json, send the Asynchronous request
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
      default:
        throw new FullContactException("Wrong API Endpoint provided for Resolve");
    }
    handleHttpResponse(
        httpRequest,
        retryHandler,
        httpResponseCompletableFuture,
        responseCF,
        FCApiEndpoint.PERSON_ENRICH);
    return responseCF.thenApply(FullContact::getResolveResponse);
  }

  /**
   * Method for Email Verification. It sends a Asynchronous request using HTTP GET method. It also
   * handles retries based RetryHandler specified on FullContact client level.
   *
   * @param email original email sent by client for verification
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
   * Method for Email Verification. It sends a Asynchronous request using HTTP GET method. It also
   * handles retries based on retry condition specified in RetryHandler.
   *
   * @param email original email sent by client for verification
   * @return completed CompletableFuture with EmailVerificationResponse
   * @throws FullContactException exception if client is shutdown
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<EmailVerificationResponse> emailVerification(
      String email, RetryHandler retryHandler) throws FullContactException {
    checkForShutdown();
    if (email != null && !email.trim().isEmpty()) {
      CompletableFuture<Response<ResponseBody>> responseCF = new CompletableFuture<>();
      RequestBody httpRequest = buildHttpRequest(email);
      CompletableFuture<Response<ResponseBody>> httpResponseCompletableFuture =
          this.client.emailVerification(email);
      handleHttpResponse(
          httpRequest,
          retryHandler,
          httpResponseCompletableFuture,
          responseCF,
          FCApiEndpoint.EMAIL_VERIFICATION);
      return responseCF.thenApply(FullContact::getEmailVerificationResponse);
    } else {
      throw new FullContactException("Email can't be empty");
    }
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
   * @return PersonResponse
   */
  protected static PersonResponse getPersonResponse(Response<ResponseBody> response) {
    PersonResponse personResponse;
    if (response.isSuccessful() && response.body() != null) {
      personResponse = gson.fromJson(response.body().charStream(), PersonResponse.class);
      if (response.code() == 200) {
        personResponse.message = FCConstants.HTTP_RESPONSE_STATUS_200_MESSAGE;
      }
    } else {
      personResponse = new PersonResponse();
      if (response.errorBody() != null) {
        personResponse = gson.fromJson(response.errorBody().charStream(), PersonResponse.class);
      } else {
        personResponse.message = response.message();
      }
    }
    personResponse.isSuccessful =
        response.code() == 200 || response.code() == 202 || response.code() == 404;
    personResponse.statusCode = response.code();
    return personResponse;
  }

  /**
   * This method creates company enrich response and handle for different response codes
   *
   * @param response raw response from company enrich API
   * @return CompanyResponse
   */
  protected static CompanyResponse getCompanyResponse(Response<ResponseBody> response) {
    CompanyResponse companyResponse;
    if (response.isSuccessful() && response.body() != null) {
      companyResponse = gson.fromJson(response.body().charStream(), CompanyResponse.class);
      if (response.code() == 200) {
        companyResponse.message = FCConstants.HTTP_RESPONSE_STATUS_200_MESSAGE;
      }
    } else {
      companyResponse = new CompanyResponse();
      if (response.errorBody() != null) {
        companyResponse = gson.fromJson(response.errorBody().charStream(), CompanyResponse.class);
      } else {
        companyResponse.message = response.message();
      }
    }
    companyResponse.isSuccessful =
        response.code() == 200 || response.code() == 202 || response.code() == 404;
    companyResponse.statusCode = response.code();
    return companyResponse;
  }

  /**
   * This method creates company search response and handle for different response codes
   *
   * @param response raw response from company search API
   * @return CompanySearchResponseList
   */
  protected static CompanySearchResponseList getCompanySearchResponse(
      Response<ResponseBody> response) {
    CompanySearchResponseList companySearchResponseList = new CompanySearchResponseList();
    if (response.body() != null) {
      if (response.code() == 200) {
        companySearchResponseList.companySearchResponses =
            gson.fromJson(response.body().charStream(), companySearchResponseType);
        companySearchResponseList.message = FCConstants.HTTP_RESPONSE_STATUS_200_MESSAGE;
        companySearchResponseList.isSuccessful = true;
      } else {
        companySearchResponseList =
            gson.fromJson(response.body().charStream(), CompanySearchResponseList.class);
      }
    } else {
      if (response.errorBody() != null) {
        companySearchResponseList =
            gson.fromJson(response.errorBody().charStream(), CompanySearchResponseList.class);
      } else {
        companySearchResponseList.message = response.message();
      }
    }
    companySearchResponseList.isSuccessful =
        (response.code() == 200) || (response.code() == 202) || (response.code() == 404);
    companySearchResponseList.statusCode = response.code();
    return companySearchResponseList;
  }

  /**
   * This method create Resolve response and handle for different response codes
   *
   * @param response raw response from Resolve APIs
   * @return ResolveResponse
   */
  protected static ResolveResponse getResolveResponse(Response<ResponseBody> response) {
    ResolveResponse resolveResponse;
    if (response.isSuccessful() && response.body() != null) {
      resolveResponse = gson.fromJson(response.body().charStream(), ResolveResponse.class);
      if (response.code() == 200 || response.code() == 204) {
        resolveResponse.message = FCConstants.HTTP_RESPONSE_STATUS_200_MESSAGE;
      }
    } else {
      resolveResponse = new ResolveResponse();
      if (response.errorBody() != null) {
        resolveResponse = gson.fromJson(response.errorBody().charStream(), ResolveResponse.class);
      } else {
        resolveResponse.message = response.message();
      }
    }
    resolveResponse.statusCode = response.code();
    resolveResponse.isSuccessful =
        (response.code() == 200) || (response.code() == 204) || (response.code() == 404);
    return resolveResponse;
  }

  protected static EmailVerificationResponse getEmailVerificationResponse(
      Response<ResponseBody> response) {
    EmailVerificationResponse emailVerificationResponse = new EmailVerificationResponse();
    if (response.isSuccessful() && response.body() != null) {
      emailVerificationResponse =
          gson.fromJson(response.body().charStream(), EmailVerificationResponse.class);
      if (response.code() == 200) {
        emailVerificationResponse.message = FCConstants.HTTP_RESPONSE_STATUS_200_MESSAGE;
      }
    } else {
      emailVerificationResponse = new EmailVerificationResponse();
      if (response.errorBody() != null) {
        emailVerificationResponse =
            gson.fromJson(response.errorBody().charStream(), EmailVerificationResponse.class);
      } else {
        emailVerificationResponse.message = response.message();
      }
    }
    emailVerificationResponse.statusCode = response.code();
    emailVerificationResponse.isSuccessful =
        (response.code() == 200) || (response.code() == 202) || (response.code() == 404);
    return emailVerificationResponse;
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
            CompletableFuture<Response<ResponseBody>> retryCF = null;
            switch (fcApiEndpoint) {
              case PERSON_ENRICH:
                retryCF = this.client.personEnrich(httpRequest);
                break;
              case COMPANY_ENRICH:
                retryCF = this.client.companyEnrich(httpRequest);
                break;
              case COMPANY_SEARCH:
                retryCF = this.client.companySearch(httpRequest);
                break;
              case IDENTITY_MAP:
                retryCF = this.client.identityMap(httpRequest);
                break;
              case IDENTITY_RESOLVE:
                retryCF = this.client.identityResolve(httpRequest);
                break;
              case IDENTITY_DELETE:
                retryCF = this.client.identityDelete(httpRequest);
                break;
              case EMAIL_VERIFICATION:
                try {
                  final Buffer buffer = new Buffer();
                  httpRequest.writeTo(buffer);
                  retryCF = this.client.emailVerification(buffer.readUtf8());
                } catch (IOException ignored) {
                }
                break;
            }
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

  /** @return Person Request Builder for Person Enrich request */
  public static PersonRequest.PersonRequestBuilder buildPersonRequest() {
    return PersonRequest.builder();
  }

  /** @return Company Request Builder for Company Enrich and Company Search requests */
  public static CompanyRequest.CompanyRequestBuilder buildCompanyRequest() {
    return CompanyRequest.builder();
  }

  /** @return Resolve Request Builder for Resolve */
  public static ResolveRequest.ResolveRequestBuilder buildResolveRequest() {
    return ResolveRequest.builder();
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
