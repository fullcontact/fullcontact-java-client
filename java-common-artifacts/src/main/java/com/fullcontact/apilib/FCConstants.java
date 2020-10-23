package com.fullcontact.apilib;

import java.net.URI;

/** Defines the constants used in FullContact client */
public class FCConstants {
  public static final String API_BASE_DEFAULT = "https://api.fullcontact.com/v3/";
  public static final String API_BASE_V2 = "https://api.fullcontact.com/v2/";

  public static final String VERSION = "2.1.1";

  // User Agent
  public static final String USER_AGENT_Java8 = "FullContact_Java8_Client_V" + VERSION;
  public static final String USER_AGENT_Java11 = "FullContact_Java11_Client_V" + VERSION;

  public static final String API_ENDPOINT_PERSON_ENRICH = "person.enrich";
  public static final String API_ENDPOINT_COMPANY_ENRICH = "company.enrich";
  public static final String API_ENDPOINT_COMPANY_SEARCH = "company.search";

  // Response Messages
  public static final String HTTP_RESPONSE_STATUS_200_MESSAGE = "OK";
  public static final String HTTP_RESPONSE_STATUS_50X_MESSAGE = "SERVER ERROR";

  // Resolve endpoints
  public static final String API_ENDPOINT_IDENTITY_MAP = "identity.map";
  public static final String API_ENDPOINT_IDENTITY_RESOLVE = "identity.resolve";
  public static final String API_ENDPOINT_IDENTITY_RESOLVE_With_TAGS = "identity.resolve?tags=true";
  public static final String API_ENDPOINT_IDENTITY_DELETE = "identity.delete";

  // Tags/Metadata endpoints
  public static final String API_ENDPOINT_TAGS_CREATE = "tags.create";
  public static final String API_ENDPOINT_TAGS_GET = "tags.get";
  public static final String API_ENDPOINT_TAGS_DELETE = "tags.delete";

  // Email Verification
  public static final String API_ENDPOINT_VERIFICATION_EMAIL = "verification/email";

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
  public static final URI identityResolveUriWithTags =
      URI.create(API_BASE_DEFAULT + API_ENDPOINT_IDENTITY_RESOLVE_With_TAGS);
  public static final URI identityDeleteUri =
      URI.create(API_BASE_DEFAULT + API_ENDPOINT_IDENTITY_DELETE);
  public static final URI tagsCreateUri = URI.create(API_BASE_DEFAULT + API_ENDPOINT_TAGS_CREATE);
  public static final URI tagsGetUri = URI.create(API_BASE_DEFAULT + API_ENDPOINT_TAGS_GET);
  public static final URI tagsDeleteUri = URI.create(API_BASE_DEFAULT + API_ENDPOINT_TAGS_DELETE);
}
