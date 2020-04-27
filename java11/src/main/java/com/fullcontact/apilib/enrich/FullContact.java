package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.FCConstants;
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
 * The FullContact class represents FullContact client. It supports V3 Person Enrich, Company Enrich
 * and Company Search. It uses in-built HttpClient for sending all requests. All requests are
 * converted to JSON and sent via POST method asynchronously
 */
public class FullContact implements AutoCloseable {
  private final String userAgent;
  private final CredentialsProvider credentialsProvider;
  private final long connectTimeoutMillis;
  private final RetryHandler retryHandler;
  private final HttpClient httpClient;
  private final Map<String, String> headers;
  private final String[] headersArray;
  private final Duration timeoutDuration;
  private final ScheduledExecutorService executor;
  private static final URI personEnrichUri =
      URI.create(FCConstants.API_BASE_DEFAULT + FCConstants.API_ENDPOINT_PERSON_ENRICH);
  private static final URI companyEnrichUri =
      URI.create(FCConstants.API_BASE_DEFAULT + FCConstants.API_ENDPOINT_COMPANY_ENRICH);
  private static final URI companySearchUri =
      URI.create(FCConstants.API_BASE_DEFAULT + FCConstants.API_ENDPOINT_COMPANY_SEARCH);
  private static final Type companySearchResponseType =
      new TypeToken<ArrayList<CompanySearchResponse>>() {}.getType();
  private static final Gson gson = new Gson();
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
      String userAgent,
      Map<String, String> headers,
      long connectTimeoutMillis,
      RetryHandler retryHandler) {
    this.credentialsProvider = credentialsProvider;
    this.retryHandler = retryHandler;
    this.userAgent = userAgent;
    this.headersArray = processHeader(headers);
    this.headers = headers;
    this.connectTimeoutMillis = connectTimeoutMillis > 0 ? connectTimeoutMillis : 3000;
    this.timeoutDuration = Duration.ofMillis(this.connectTimeoutMillis);
    this.httpClient = configureHttpClient();
    this.executor = new ScheduledThreadPoolExecutor(5);
  }

  /** Method to process custom headers, adding auth key and converting to array */
  private String[] processHeader(Map<String, String> customHeaders) {
    Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    if (customHeaders != null && !customHeaders.isEmpty()) {
      headers.putAll(customHeaders);
    }
    headers.put("Authorization", "Bearer " + this.credentialsProvider.getApiKey());
    headers.put("Content-Type", "application/json");
    if (this.userAgent != null && !this.userAgent.isBlank()) {
      headers.put("User-Agent", this.userAgent);
    }
    return headers.entrySet().stream()
        .filter(entry -> entry.getValue() != null)
        .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList())
        .toArray(String[]::new);
  }

  /** Builds a new HttpClient with specified properties */
  private HttpClient configureHttpClient() {
    return HttpClient.newBuilder().connectTimeout(this.timeoutDuration).build();
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
    CompletableFuture<HttpResponse<String>> responseCompletableFutureResult =
        new CompletableFuture<>();
    HttpRequest httpRequest =
        HttpRequest.newBuilder(personEnrichUri)
            .headers(this.headersArray)
            .timeout(this.timeoutDuration)
            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(personRequest)))
            .build();

    CompletableFuture<HttpResponse<String>> httpResponseCompletableFuture =
        this.httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
    httpResponseCompletableFuture.handle(
        (httpResponse, throwable) -> {
          if (throwable != null) {
            handleAutoRetry(
                responseCompletableFutureResult,
                httpResponse,
                httpRequest,
                throwable,
                0,
                retryHandler);
          } else if (httpResponse != null && !retryHandler.shouldRetry(httpResponse.statusCode())) {
            responseCompletableFutureResult.complete(httpResponse);
          } else {
            handleAutoRetry(
                responseCompletableFutureResult, httpResponse, httpRequest, null, 0, retryHandler);
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
    CompletableFuture<HttpResponse<String>> responseCompletableFutureResult =
        new CompletableFuture<>();
    HttpRequest httpRequest =
        HttpRequest.newBuilder(companyEnrichUri)
            .headers(this.headersArray)
            .timeout(this.timeoutDuration)
            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(companyRequest)))
            .build();
    CompletableFuture<HttpResponse<String>> httpResponseCompletableFuture =
        this.httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());

    httpResponseCompletableFuture.handle(
        (httpResponse, throwable) -> {
          if (throwable != null) {
            handleAutoRetry(
                responseCompletableFutureResult,
                httpResponse,
                httpRequest,
                throwable,
                0,
                retryHandler);
          } else if (httpResponse != null && !retryHandler.shouldRetry(httpResponse.statusCode())) {
            responseCompletableFutureResult.complete(httpResponse);
          } else {
            handleAutoRetry(
                responseCompletableFutureResult, httpResponse, httpRequest, null, 0, retryHandler);
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
    CompletableFuture<HttpResponse<String>> responseCompletableFutureResult =
        new CompletableFuture<>();
    HttpRequest httpRequest =
        HttpRequest.newBuilder(companySearchUri)
            .headers(this.headersArray)
            .timeout(this.timeoutDuration)
            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(companyRequest)))
            .build();

    CompletableFuture<HttpResponse<String>> httpResponseCompletableFuture =
        this.httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());

    httpResponseCompletableFuture.handle(
        (httpResponse, throwable) -> {
          if (throwable != null) {
            handleAutoRetry(
                responseCompletableFutureResult,
                httpResponse,
                httpRequest,
                throwable,
                0,
                retryHandler);
          } else if (httpResponse != null && !retryHandler.shouldRetry(httpResponse.statusCode())) {
            responseCompletableFutureResult.complete(httpResponse);
          } else {
            handleAutoRetry(
                responseCompletableFutureResult, httpResponse, httpRequest, null, 0, retryHandler);
          }
          return null;
        });
    return responseCompletableFutureResult.thenApply(FullContact::getCompanySearchResponseList);
  }

  /**
   * This method resolves person enrich response and handle for different response codes
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
   * This method resolves company enrich response and handle for different response codes
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
   * This method resolves company search response and handle for different response codes
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
   * This method handles Auto Retry in case retry condition is true. It keeps retrying till the
   * retryAttempts exhaust or the response is successful and completes the
   * responseCompletableFutureResult based on result. For retrying, it schedules the request using
   * ScheduledThreadPoolExecutor with retryDelayMillis delay time.
   *
   * @param httpRequest reusing the same httpRequest built in enrich method
   * @param httpResponse response of the last retry, used to complete
   *     responseCompletableFutureResult if all retry attempts exhaust
   * @param responseCompletableFutureResult result completableFuture which is completed here and
   *     returned to client
   * @param throwable exception from last retry, used to completeExceptionally
   *     responseCompletableFutureResult if all retries exhaust
   * @param retryAttemptsDone track the number of retry attempts already done
   */
  protected void handleAutoRetry(
      CompletableFuture<HttpResponse<String>> responseCompletableFutureResult,
      HttpResponse<String> httpResponse,
      HttpRequest httpRequest,
      Throwable throwable,
      int retryAttemptsDone,
      RetryHandler retryHandler) {
    if (retryAttemptsDone < retryHandler.getRetryAttempts()) {
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
                        responseCompletableFutureResult,
                        retryHttpResponse,
                        httpRequest,
                        retryThrowable,
                        finalRetryAttemptsDone,
                        retryHandler);
                  } else if (!retryHandler.shouldRetry(retryHttpResponse.statusCode())) {
                    responseCompletableFutureResult.complete(retryHttpResponse);
                  } else {
                    handleAutoRetry(
                        responseCompletableFutureResult,
                        retryHttpResponse,
                        httpRequest,
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
      responseCompletableFutureResult.complete(httpResponse);
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
      return new FullContact(
          credentialsProvider, userAgent, headers, connectTimeoutMillis, retryHandler);
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
