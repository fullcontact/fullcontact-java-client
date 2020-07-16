package com.fullcontact.apilib;

import com.google.gson.JsonElement;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.*;

import java.util.concurrent.CompletableFuture;

/**
 * Defines all the Retrofit endpoints for the FullContact API. All requests are HTTP POST type with
 * Asynchronous processing and returns a CompletableFuture
 */
public interface FullContactApi {

  @POST(FCConstants.API_ENDPOINT_PERSON_ENRICH)
  CompletableFuture<Response<JsonElement>> personEnrich(@Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_COMPANY_ENRICH)
  CompletableFuture<Response<JsonElement>> companyEnrich(@Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_COMPANY_SEARCH)
  CompletableFuture<Response<JsonElement>> companySearch(@Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_IDENTITY_MAP)
  CompletableFuture<Response<JsonElement>> identityMap(@Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_IDENTITY_RESOLVE)
  CompletableFuture<Response<JsonElement>> identityResolve(@Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_IDENTITY_DELETE)
  CompletableFuture<Response<JsonElement>> identityDelete(@Body RequestBody body);

  @GET(FCConstants.API_ENDPOINT_VERIFICATION_EMAIL)
  CompletableFuture<Response<JsonElement>> emailVerification(@Query("email") String email);
}
