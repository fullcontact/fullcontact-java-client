package com.fullcontact.apilib.test;

import okhttp3.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

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
        case "tc_303":
          fileName = "src/test/resources/empty.json";
          statusCode = 204;
          message = "OK";
          break;
        case "tc_104":
          fileName = "src/test/resources/identityResolveResponseWithTags.json";
          statusCode = 200;
          message = "OK";
          break;
        case "tc_301":
          fileName = "src/test/resources/tagsCreateResponse.json";
          statusCode = 200;
          message = "OK";
          break;
        case "tc_302":
          fileName = "src/test/resources/tagsGetResponse.json";
          statusCode = 200;
          message = "OK";
          break;
        case "tc_401":
          fileName = "src/test/resources/audienceCreateResponse.json";
          statusCode = 200;
          message = "OK";
          break;
        case "tc_402":
          fileName = "src/test/resources/audienceDownloadResponse.json";
          statusCode = 200;
          message = "OK";
          break;
        case "tc_501":
          statusCode = 202;
          fileName = null;
          message = "Accepted";
          break;
        case "tc_502":
          statusCode = 200;
          fileName = "src/test/resources/permissionResponseList.json";
          message = "OK";
          break;
        case "tc_503":
          statusCode = 200;
          fileName = "src/test/resources/permissionCurrentResponse.json";
          message = "OK";
          break;
        case "tc_504":
          statusCode = 200;
          fileName = "src/test/resources/permissionVerifyResponse.json";
          message = "OK";
          break;
        default:
          fileName = "";
          statusCode = 500;
          break;
      }
    }
    if (fileName == null) {
      response =
          new Response.Builder()
              .code(statusCode)
              .message(message)
              .request(chain.request())
              .protocol(Protocol.HTTP_1_0)
              .body(ResponseBody.create(MediaType.parse(mContentType), ""))
              .addHeader("content-type", mContentType)
              .build();
      return response;
    }
    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
      String line;
      StringBuilder sb = new StringBuilder();
      while ((line = br.readLine()) != null) {
        sb.append(line.trim());
      }
      if (testCode.equals("tc_402")) {
        response =
            new Response.Builder()
                .code(statusCode)
                .message(message)
                .request(chain.request())
                .protocol(Protocol.HTTP_1_0)
                .body(
                    ResponseBody.create(
                        MediaType.parse("application/octet-stream"), compress(sb.toString())))
                .addHeader("content-type", "application/octet-stream")
                .build();
      } else {
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
    }
    return response;
  }

  public static byte[] compress(String data) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
    GZIPOutputStream gzip = new GZIPOutputStream(bos);
    gzip.write(data.getBytes());
    gzip.close();
    byte[] compressed = bos.toByteArray();
    bos.close();
    return compressed;
  }
}
