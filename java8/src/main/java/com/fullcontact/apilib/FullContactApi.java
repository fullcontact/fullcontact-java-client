package com.fullcontact.apilib;

import com.fullcontact.apilib.models.Request.CompanyRequest;
import com.fullcontact.apilib.models.Request.PersonRequest;
import com.fullcontact.apilib.models.Response.CompanyResponse;
import com.fullcontact.apilib.models.Response.PersonResponse;
import com.google.gson.JsonElement;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.concurrent.CompletableFuture;

/**
 * Defines all the Retrofit endpoints for the FullContact API. All requests are HTTP POST type with
 * Asynchronous processing and returns a CompletableFuture
 */
public interface FullContactApi {

  @POST(FCConstants.API_ENDPOINT_PERSON_ENRICH)
  CompletableFuture<Response<PersonResponse>> personEnrich(@Body PersonRequest personRequest);

  @POST(FCConstants.API_ENDPOINT_COMPANY_ENRICH)
  CompletableFuture<Response<CompanyResponse>> companyEnrich(@Body CompanyRequest companyRequest);

  @POST(FCConstants.API_ENDPOINT_COMPANY_SEARCH)
  CompletableFuture<Response<JsonElement>> companySearch(@Body CompanyRequest companyRequest);
}
