package com.fullcontact.apilib;

import java.net.URI;

/** Defines the constants used in FullContact client */
public class FCConstants {
  public static final String API_BASE_DEFAULT = "https://api.fullcontact.com/v3/";
  public static final String API_ENDPOINT_PERSON_ENRICH = "person.enrich";
  public static final String API_ENDPOINT_COMPANY_ENRICH = "company.enrich";
  public static final String API_ENDPOINT_COMPANY_SEARCH = "company.search";
  public static final String HTTP_RESPONSE_STATUS_200_MESSAGE = "OK";
  public static final String HTTP_RESPONSE_STATUS_50X_MESSAGE = "SERVER ERROR";

  // Resolve endpoints
  public static final String API_ENDPOINT_IDENTITY_MAP = "identity.map";
  public static final String API_ENDPOINT_IDENTITY_RESOLVE = "identity.resolve";
  public static final String API_ENDPOINT_IDENTITY_DELETE = "identity.delete";

  // URIs
  public static final URI personEnrichUri =
      URI.create(API_BASE_DEFAULT + API_ENDPOINT_PERSON_ENRICH);
  public static final URI companyEnrichUri =
      URI.create(API_BASE_DEFAULT + API_ENDPOINT_COMPANY_ENRICH);
  public static final URI companySearchUri =
      URI.create(API_BASE_DEFAULT + API_ENDPOINT_COMPANY_SEARCH);
  public static final URI identityMapUri = URI.create(API_BASE_DEFAULT + API_ENDPOINT_IDENTITY_MAP);
  public static final URI identityResolveUri =
      URI.create(API_BASE_DEFAULT + API_ENDPOINT_IDENTITY_RESOLVE);
  public static final URI identityDeleteUri =
      URI.create(API_BASE_DEFAULT + API_ENDPOINT_IDENTITY_DELETE);
}
