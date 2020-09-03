package com.fullcontact.apilib.test;

import okhttp3.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MockInterceptor implements Interceptor {

  private String mContentType = "application/json";

  @Override
  public Response intercept(Chain chain) throws IOException {
    Response response;
    String fileName = "", message = "";
    int statusCode = 500;
    String testCode = chain.request().headers().get("testCode");
    if (testCode != null) {
      switch (testCode) {
        case "tc_001":
          fileName = "src/test/resources/completeCanaryResponse.json";
          statusCode = 200;
          message = "OK";
          break;
        case "tc_002":
          fileName = "src/test/resources/status400_1.json";
          statusCode = 400;
          message = "BadRequest";
          break;
        case "tc_003":
          fileName = "src/test/resources/status202.json";
          statusCode = 202;
          message = "Accepted";
          break;
        case "tc_004":
          fileName = "src/test/resources/status401.json";
          statusCode = 401;
          message = "Unauthorized";
          break;
        case "tc_005":
          fileName = "src/test/resources/status404.json";
          statusCode = 404;
          message = "Not Found";
          break;
        case "tc_006":
          fileName = "src/test/resources/status403.json";
          statusCode = 403;
          message = "API Key is missing or invalid.";
          break;
        case "tc_007":
          fileName = "src/test/resources/status422.json";
          statusCode = 422;
          message = "Input domain parameter (\"fullcontact\") does not contain a valid domain.";
          break;
        case "tc_051":
          fileName = "src/test/resources/companyEnrichResponse.json";
          statusCode = 200;
          message = "OK";
          break;
        case "tc_071":
          fileName = "src/test/resources/companySearchResponse.json";
          statusCode = 200;
          message = "OK";
          break;
        case "tc_101":
          fileName = "src/test/resources/identityMapResponse.json";
          statusCode = 200;
          message = "OK";
          break;
        case "tc_102":
          fileName = "src/test/resources/identityResolveResponse.json";
          statusCode = 200;
          message = "OK";
          break;
        case "tc_103":
          fileName = "src/test/resources/empty.json";
          statusCode = 204;
          message = "OK";
          break;
        case "tc_201":
          fileName = "src/test/resources/emailVerificationResponse.json";
          statusCode = 200;
          message = "OK";
          break;
        default:
          fileName = "";
          statusCode = 500;
          break;
      }
    }
    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
      String line;
      StringBuilder sb = new StringBuilder();
      while ((line = br.readLine()) != null) {
        sb.append(line.trim());
      }
      response =
          new Response.Builder()
              .code(statusCode)
              .message(message)
              .request(chain.request())
              .protocol(Protocol.HTTP_1_0)
              .body(ResponseBody.create(MediaType.parse(mContentType), sb.toString().getBytes()))
              .addHeader("content-type", mContentType)
              .build();
    }
    return response;
  }
}
