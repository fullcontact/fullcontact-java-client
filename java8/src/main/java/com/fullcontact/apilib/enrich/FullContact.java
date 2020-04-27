package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.FCConstants;
import com.fullcontact.apilib.FullContactApi;
import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.auth.CredentialsProvider;
import com.fullcontact.apilib.auth.DefaultCredentialProvider;
import com.fullcontact.apilib.models.Request.CompanyRequest;
import com.fullcontact.apilib.models.Request.PersonRequest;
import com.fullcontact.apilib.models.Response.CompanyResponse;
import com.fullcontact.apilib.models.Response.CompanySearchResponse;
import com.fullcontact.apilib.models.Response.CompanySearchResponseList;
import com.fullcontact.apilib.models.Response.PersonResponse;
import com.fullcontact.apilib.retry.DefaultRetryHandler;
import com.fullcontact.apilib.retry.RetryHandler;
import com.fullcontact.apilib.test.MockInterceptor;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import lombok.Builder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
 * and Company Search. It uses Retrofit for sending all requests. All requests are converted to JSON
 * and sent via POST method asynchronously
 */
public class FullContact implements AutoCloseable {
  private final String baseUrl = FCConstants.API_BASE_DEFAULT;
  private final OkHttpClient httpClient;
  private final FullContactApi client;
  private final CredentialsProvider credentialsProvider;
  private final RetryHandler retryHandler;
  private final Map<String, String> headers;
  private final String userAgent;
  private final long connectTimeoutMillis;
  private final ScheduledExecutorService executor;
  private static final Gson gson = new Gson();
  private static final Type companySearchResponseType =
      new TypeToken<ArrayList<CompanySearchResponse>>() {}.getType();
  private boolean isShutdown = false;

  /**
   * FullContact client constructor used to initialise the client
   *
   * @param credentialsProvider for auth
   * @param headers custom client headers
   * @param userAgent client UserAgent
   * @param connectTimeoutMillis connection timout for all requests
   * @param retryHandler RetryHandler specified for client
   */
  @Builder
  public FullContact(
      CredentialsProvider credentialsProvider,
      Map<String, String> headers,
      String userAgent,
      long connectTimeoutMillis,
      RetryHandler retryHandler) {
    this.credentialsProvider = credentialsProvider;
    this.retryHandler = retryHandler;
    this.headers = headers != null ? Collections.unmodifiableMap(headers) : null;
    this.userAgent = userAgent;
    this.httpClient = this.configureHTTPClientBuilder().build();
    this.client = this.configureRetrofit().create(FullContactApi.class);
    this.connectTimeoutMillis = connectTimeoutMillis > 0 ? connectTimeoutMillis : 3000;
    this.executor = new ScheduledThreadPoolExecutor(5);
  }
  /**
   * Method to build and create OkHttpClient. All the custom headers and auth key is added here.
   *
   * @return OkHttpClient Builder
   */
  private OkHttpClient.Builder configureHTTPClientBuilder() {
    OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
    httpClientBuilder.addInterceptor(
        chain -> {
          Request.Builder requestBuilder = chain.request().newBuilder();
          requestBuilder.addHeader("Authorization", "Bearer " + credentialsProvider.getApiKey());
          requestBuilder.addHeader("Content-Type", "application/json");
          if (this.userAgent != null && !this.userAgent.trim().isEmpty()) {
            requestBuilder.addHeader("User-Agent", this.userAgent);
          }
          if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
              if (!entry.getKey().equalsIgnoreCase("authorization")
                  && !entry.getKey().equalsIgnoreCase("Content-Type")
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
    httpClientBuilder.connectTimeout(this.connectTimeoutMillis, TimeUnit.MILLISECONDS);

    return httpClientBuilder;
  }
  /**
   * Method to create Retrofit client using httpClient and Gson converter.
   *
   * @return Retrofit client
   */
  private Retrofit configureRetrofit() {
    return new Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(this.baseUrl)
        .client(this.httpClient)
        .build();
  }

  /**
   * Method for Person Enrich without any custom RetryHandler, it passes the request to enrich
   * method with retryHandler specified at Fullcontact Client level.
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
    if (isShutdown) {
      throw new FullContactException("FullContact client is shutdown. Please create a new client");
    }
    CompletableFuture<retrofit2.Response<PersonResponse>> responseCompletableFutureResult =
        new CompletableFuture<>();
    CompletableFuture<retrofit2.Response<PersonResponse>> responseCompletableFuture =
        this.client.personEnrich(personRequest);
    responseCompletableFuture.handle(
        (httpResponse, throwable) -> {
          if (throwable != null) {
            handleAutoRetryForPersonEnrich(
                responseCompletableFutureResult,
                httpResponse,
                personRequest,
                throwable,
                0,
                retryHandler);
          } else if (!retryHandler.shouldRetry(httpResponse.code())) {
            responseCompletableFutureResult.complete(httpResponse);
          } else {
            handleAutoRetryForPersonEnrich(
                responseCompletableFutureResult,
                httpResponse,
                personRequest,
                null,
                0,
                retryHandler);
          }
          return null;
        });

    return responseCompletableFutureResult.thenApply(FullContact::getPersonResponse);
  }

  /**
   * Method for Company Enrich without any custom RetryHandler, it passes the request to enrich
   * method with retryHandler specified at Fullcontact Client level.
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
    if (isShutdown) {
      throw new FullContactException("FullContact client is shutdown. Please create a new client");
    }
    companyRequest.validateForEnrich();
    CompletableFuture<retrofit2.Response<CompanyResponse>> responseCompletableFutureResult =
        new CompletableFuture<>();
    CompletableFuture<retrofit2.Response<CompanyResponse>> responseCompletableFuture =
        this.client.companyEnrich(companyRequest);
    responseCompletableFuture.handle(
        (httpResponse, throwable) -> {
          if (throwable != null) {
            handleAutoRetryForCompanyEnrich(
                responseCompletableFutureResult,
                httpResponse,
                companyRequest,
                throwable,
                0,
                retryHandler);
          } else if (!retryHandler.shouldRetry(httpResponse.code())) {
            responseCompletableFutureResult.complete(httpResponse);
          } else {
            handleAutoRetryForCompanyEnrich(
                responseCompletableFutureResult,
                httpResponse,
                companyRequest,
                null,
                0,
                retryHandler);
          }
          return null;
        });
    return responseCompletableFutureResult.thenApply(FullContact::getCompanyResponse);
  }

  /**
   * Method for Company Search without any custom RetryHandler, it passes the request to search
   * method with retryHandler specified at Fullcontact Client level.
   *
   * @param companyRequest original request sent by client
   * @return completed CompletableFuture with CompanySearchResponseList
   * @throws FullContactException exception if client is shutdown or request validation
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
   * @throws FullContactException exception if client is shutdown or request validation
   * @see <a href =
   *     "https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture</a>
   */
  public CompletableFuture<CompanySearchResponseList> search(
      CompanyRequest companyRequest, RetryHandler retryHandler) throws FullContactException {
    if (isShutdown) {
      throw new FullContactException("FullContact client is shutdown. Please create a new client");
    }
    companyRequest.validateForSearch();
    CompletableFuture<retrofit2.Response<JsonElement>> responseCompletableFutureResult =
        new CompletableFuture<>();
    CompletableFuture<retrofit2.Response<JsonElement>> companySearchResponseListCompletableFuture =
        this.client.companySearch(companyRequest);
    companySearchResponseListCompletableFuture.handle(
        (companySearchResponse, throwable) -> {
          if (throwable != null) {
            handleAutoRetryForCompanySearch(
                responseCompletableFutureResult,
                companySearchResponse,
                companyRequest,
                throwable,
                0,
                retryHandler);
          } else if (!retryHandler.shouldRetry(companySearchResponse.code())) {
            responseCompletableFutureResult.complete(companySearchResponse);
          } else {
            handleAutoRetryForCompanySearch(
                responseCompletableFutureResult,
                companySearchResponse,
                companyRequest,
                null,
                0,
                retryHandler);
          }
          return null;
        });
    return responseCompletableFutureResult.thenApply(FullContact::getCompanySearchResponse);
  }

  /**
   * This method resolves person enrich response and handle for different response codes
   *
   * @param response raw response from person enrich API
   * @return PersonResponse
   */
  protected static PersonResponse getPersonResponse(retrofit2.Response<PersonResponse> response) {
    PersonResponse personResponse;
    if (response.isSuccessful() && response.body() != null) {
      personResponse = response.body();
      if (response.code() == 200) {
        personResponse.message = FCConstants.HTTP_RESPONSE_STATUS_200_MESSAGE;
      }
    } else {
      personResponse = new PersonResponse();
      personResponse.message = response.message();
    }
    personResponse.isSuccessful =
        response.code() == 200 || response.code() == 202 || response.code() == 404;
    personResponse.statusCode = response.code();
    return personResponse;
  }

  /**
   * This method resolves company enrich response and handle for different response codes
   *
   * @param response raw response from company enrich API
   * @return CompanyResponse
   */
  protected static CompanyResponse getCompanyResponse(
      retrofit2.Response<CompanyResponse> response) {
    CompanyResponse companyResponse;
    if (response.isSuccessful() && response.body() != null) {
      companyResponse = response.body();
      if (response.code() == 200) {
        companyResponse.message = FCConstants.HTTP_RESPONSE_STATUS_200_MESSAGE;
      }
    } else {
      companyResponse = new CompanyResponse();
      companyResponse.message = response.message();
    }
    companyResponse.isSuccessful =
        response.code() == 200 || response.code() == 202 || response.code() == 404;
    companyResponse.statusCode = response.code();
    return companyResponse;
  }
  /**
   * This method resolves company search response and handle for different response codes
   *
   * @param response raw response from company search API
   * @return CompanySearchResponseList
   */
  protected static CompanySearchResponseList getCompanySearchResponse(
      retrofit2.Response<JsonElement> response) {
    CompanySearchResponseList companySearchResponseList = new CompanySearchResponseList();
    if (response.body() != null && !response.body().isJsonNull()) {
      if (response.code() == 200) {
        companySearchResponseList.companySearchResponses =
            gson.fromJson(response.body(), companySearchResponseType);
        companySearchResponseList.message = FCConstants.HTTP_RESPONSE_STATUS_200_MESSAGE;
        companySearchResponseList.isSuccessful = true;
      } else {
        companySearchResponseList = gson.fromJson(response.body(), CompanySearchResponseList.class);
      }
    } else {
      companySearchResponseList.message = response.message();
    }
    companySearchResponseList.isSuccessful =
        (response.code() == 200) || (response.code() == 202) || (response.code() == 404);
    companySearchResponseList.statusCode = response.code();
    return companySearchResponseList;
  }

  /**
   * This method handles Auto Retry in case retry condition is true. It keeps retrying till the
   * retryAttempts exhaust or the response is successful and completes the
   * responseCompletableFutureResult based on result. For retrying, it schedules the request using
   * ScheduledThreadPoolExecutor with retryDelayMillis delay time.
   *
   * @param personRequest original request by client
   * @param personResponse response of the last retry, used to complete
   *     responseCompletableFutureResult if all retry attempts exhaust
   * @param responseCompletableFutureResult result completableFuture which is completed here and
   *     returned to client
   * @param throwable exception from last retry, used to completeExceptionally
   *     responseCompletableFutureResult if all retries exhaust
   * @param retryAttemptsDone track the number of retry attempts already done
   */
  protected void handleAutoRetryForPersonEnrich(
      CompletableFuture<Response<PersonResponse>> responseCompletableFutureResult,
      Response<PersonResponse> personResponse,
      PersonRequest personRequest,
      Throwable throwable,
      int retryAttemptsDone,
      RetryHandler retryHandler) {
    if (retryAttemptsDone < retryHandler.getRetryAttempts()) {
      retryAttemptsDone++;
      int finalRetryAttemptsDone = retryAttemptsDone;
      this.executor.schedule(
          () -> {
            CompletableFuture<Response<PersonResponse>> retryCompletableFuture =
                this.client.personEnrich(personRequest);
            retryCompletableFuture.handle(
                (retryPersonResponse, retryThrowable) -> {
                  if (retryThrowable != null) {
                    handleAutoRetryForPersonEnrich(
                        responseCompletableFutureResult,
                        retryPersonResponse,
                        personRequest,
                        retryThrowable,
                        finalRetryAttemptsDone,
                        retryHandler);
                  } else if (!retryHandler.shouldRetry(retryPersonResponse.code())) {
                    responseCompletableFutureResult.complete(retryPersonResponse);
                  } else {
                    handleAutoRetryForPersonEnrich(
                        responseCompletableFutureResult,
                        retryPersonResponse,
                        personRequest,
                        null,
                        finalRetryAttemptsDone,
                        retryHandler);
                  }
                  return null;
                });
          },
          retryHandler.getRetryDelayMillis() * (long) Math.pow(2, retryAttemptsDone - 1),
          TimeUnit.MILLISECONDS);
    } else if (throwable != null && retryAttemptsDone == retryHandler.getRetryAttempts()) {
      responseCompletableFutureResult.completeExceptionally(throwable);
    } else {
      responseCompletableFutureResult.complete(personResponse);
    }
  }

  /**
   * This method handles Auto Retry in case retry condition is true. It keeps retrying till the
   * retryAttempts exhaust or the response is successful and completes the
   * responseCompletableFutureResult based on result. For retrying, it schedules the request using
   * ScheduledThreadPoolExecutor with retryDelayMillis delay time.
   *
   * @param companyRequest original request by client
   * @param companyResponse response of the last retry, used to complete
   *     responseCompletableFutureResult if all retry attempts exhaust
   * @param responseCompletableFutureResult result completableFuture which is completed here and
   *     returned to client
   * @param throwable exception from last retry, used to completeExceptionally
   *     responseCompletableFutureResult if all retries exhaust
   * @param retryAttemptsDone track the number of retry attempts already done
   */
  protected void handleAutoRetryForCompanyEnrich(
      CompletableFuture<Response<CompanyResponse>> responseCompletableFutureResult,
      Response<CompanyResponse> companyResponse,
      CompanyRequest companyRequest,
      Throwable throwable,
      int retryAttemptsDone,
      RetryHandler retryHandler) {
    if (retryAttemptsDone < retryHandler.getRetryAttempts()) {
      retryAttemptsDone++;
      int finalRetryAttemptsDone = retryAttemptsDone;
      this.executor.schedule(
          () -> {
            CompletableFuture<Response<CompanyResponse>> retryCompletableFuture =
                this.client.companyEnrich(companyRequest);
            retryCompletableFuture.handle(
                (retryHttpResponse, retryThrowable) -> {
                  if (retryThrowable != null) {
                    handleAutoRetryForCompanyEnrich(
                        responseCompletableFutureResult,
                        retryHttpResponse,
                        companyRequest,
                        retryThrowable,
                        finalRetryAttemptsDone,
                        retryHandler);
                  } else if (!retryHandler.shouldRetry(retryHttpResponse.code())) {
                    responseCompletableFutureResult.complete(retryHttpResponse);
                  } else {
                    handleAutoRetryForCompanyEnrich(
                        responseCompletableFutureResult,
                        retryHttpResponse,
                        companyRequest,
                        null,
                        finalRetryAttemptsDone,
                        retryHandler);
                  }
                  return null;
                });
          },
          retryHandler.getRetryDelayMillis() * (long) Math.pow(2, retryAttemptsDone - 1),
          TimeUnit.MILLISECONDS);
    } else if (throwable != null && retryAttemptsDone == retryHandler.getRetryAttempts()) {
      responseCompletableFutureResult.completeExceptionally(throwable);
    } else {
      responseCompletableFutureResult.complete(companyResponse);
    }
  }
  /**
   * This method handles Auto Retry in case retry condition is true. It keeps retrying till the
   * retryAttempts exhaust or the response is successful and completes the
   * responseCompletableFutureResult based on result. For retrying, it schedules the request using
   * ScheduledThreadPoolExecutor with retryDelayMillis delay time.
   *
   * @param companyRequest reusing the same httpRequest built in enrich method
   * @param companySearchResponse response of the last retry, used to complete
   *     responseCompletableFutureResult if all retry attempts exhaust
   * @param responseCompletableFutureResult result completableFuture which is completed here and
   *     returned to client
   * @param throwable exception from last retry, used to completeExceptionally
   *     responseCompletableFutureResult if all retries exhaust
   * @param retryAttemptsDone track the number of retry attempts already done
   */
  protected void handleAutoRetryForCompanySearch(
      CompletableFuture<Response<JsonElement>> responseCompletableFutureResult,
      Response<JsonElement> companySearchResponse,
      CompanyRequest companyRequest,
      Throwable throwable,
      int retryAttemptsDone,
      RetryHandler retryHandler) {
    if (retryAttemptsDone < retryHandler.getRetryAttempts()) {
      retryAttemptsDone++;
      int finalRetryAttemptsDone = retryAttemptsDone;
      this.executor.schedule(
          () -> {
            CompletableFuture<Response<JsonElement>> retryCompletableFuture =
                this.client.companySearch(companyRequest);
            retryCompletableFuture.handle(
                (retryHttpResponse, retryThrowable) -> {
                  if (retryThrowable != null) {
                    handleAutoRetryForCompanySearch(
                        responseCompletableFutureResult,
                        retryHttpResponse,
                        companyRequest,
                        retryThrowable,
                        finalRetryAttemptsDone,
                        retryHandler);
                  } else if (!retryHandler.shouldRetry(retryHttpResponse.code())) {
                    responseCompletableFutureResult.complete(retryHttpResponse);
                  } else {
                    handleAutoRetryForCompanySearch(
                        responseCompletableFutureResult,
                        retryHttpResponse,
                        companyRequest,
                        null,
                        finalRetryAttemptsDone,
                        retryHandler);
                  }
                  return null;
                });
          },
          retryHandler.getRetryDelayMillis() * (long) Math.pow(2, retryAttemptsDone - 1),
          TimeUnit.MILLISECONDS);
    } else if (throwable != null && retryAttemptsDone == retryHandler.getRetryAttempts()) {
      responseCompletableFutureResult.completeExceptionally(throwable);
    } else {
      responseCompletableFutureResult.complete(companySearchResponse);
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
      return new FullContact(
          credentialsProvider, headers, userAgent, connectTimeoutMillis, retryHandler);
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
     * Builder method to provide UserAgent
     *
     * @param userAgent the UserAgent of client
     * @return FullContactBuilder
     */
    public FullContactBuilder userAgent(String userAgent) {
      this.userAgent = userAgent;
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
