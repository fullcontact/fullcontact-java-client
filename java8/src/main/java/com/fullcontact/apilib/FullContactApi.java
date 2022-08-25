package com.fullcontact.apilib;

import java.util.concurrent.CompletableFuture;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Defines all the Retrofit endpoints for the FullContact API. All requests are HTTP POST type with
 * Asynchronous processing and returns a CompletableFuture
 */
public interface FullContactApi {

  @POST(FCConstants.API_ENDPOINT_PERSON_ENRICH)
  CompletableFuture<Response<ResponseBody>> personEnrich(@Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_COMPANY_ENRICH)
  CompletableFuture<Response<ResponseBody>> companyEnrich(@Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_IDENTITY_MAP)
  CompletableFuture<Response<ResponseBody>> identityMap(@Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_IDENTITY_RESOLVE)
  CompletableFuture<Response<ResponseBody>> identityResolve(@Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_IDENTITY_RESOLVE)
  CompletableFuture<Response<ResponseBody>> identityResolveWithTags(
      @Query("tags") boolean includeTags, @Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_IDENTITY_DELETE)
  CompletableFuture<Response<ResponseBody>> identityDelete(@Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_IDENTITY_MAP_RESOLVE)
  CompletableFuture<Response<ResponseBody>> identityMapResolve(@Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_TAGS_CREATE)
  CompletableFuture<Response<ResponseBody>> tagsCreate(@Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_TAGS_GET)
  CompletableFuture<Response<ResponseBody>> tagsGet(@Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_TAGS_DELETE)
  CompletableFuture<Response<ResponseBody>> tagsDelete(@Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_AUDIENCE_CREATE)
  CompletableFuture<Response<ResponseBody>> audienceCreate(@Body RequestBody body);

  @GET(FCConstants.API_ENDPOINT_AUDIENCE_DOWNLOAD)
  CompletableFuture<Response<ResponseBody>> audienceDownload(@Query("requestId") String requestId);

  @POST(FCConstants.API_ENDPOINT_PERMISSION_CREATE)
  CompletableFuture<Response<ResponseBody>> permissionCreate(@Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_PERMISSION_DELETE)
  CompletableFuture<Response<ResponseBody>> permissionDelete(@Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_PERMISSION_FIND)
  CompletableFuture<Response<ResponseBody>> permissionFind(@Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_PERMISSION_CURRENT)
  CompletableFuture<Response<ResponseBody>> permissionCurrent(@Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_PERMISSION_VERIFY)
  CompletableFuture<Response<ResponseBody>> permissionVerify(@Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_VERIFY_SIGNALS)
  CompletableFuture<Response<ResponseBody>> verifySignals(@Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_VERIFY_MATCH)
  CompletableFuture<Response<ResponseBody>> verifyMatch(@Body RequestBody body);

  @POST(FCConstants.API_ENDPOINT_VERIFY_ACTIVITY)
  CompletableFuture<Response<ResponseBody>> verifyActivity(@Body RequestBody body);
}
