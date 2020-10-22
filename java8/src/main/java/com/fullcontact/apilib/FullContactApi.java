package com.fullcontact.apilib;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.concurrent.CompletableFuture;

/**
 * Defines all the Retrofit endpoints for the FullContact API. All requests are HTTP POST type with
 * Asynchronous processing and returns a CompletableFuture
 */
public interface FullContactApi {

  @POST(FCConstants.API_ENDPOINT_PERSON_ENRICH)
  CompletableFuture<Response<ResponseBody>> personEnrich(@Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_COMPANY_ENRICH)
  CompletableFuture<Response<ResponseBody>> companyEnrich(@Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_COMPANY_SEARCH)
  CompletableFuture<Response<ResponseBody>> companySearch(@Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_IDENTITY_MAP)
  CompletableFuture<Response<ResponseBody>> identityMap(@Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_IDENTITY_RESOLVE)
  CompletableFuture<Response<ResponseBody>> identityResolve(@Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_IDENTITY_RESOLVE)
  CompletableFuture<Response<ResponseBody>> identityResolveWithTags(
      @Query("tags") boolean includeTags, @Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_IDENTITY_DELETE)
  CompletableFuture<Response<ResponseBody>> identityDelete(@Body RequestBody body);

  @GET(FCConstants.API_ENDPOINT_VERIFICATION_EMAIL)
  CompletableFuture<Response<ResponseBody>> emailVerification(@Query("email") String email);
}
