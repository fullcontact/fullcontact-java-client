package com.fullcontact.apilib.enrich;

import javax.net.ssl.SSLSession;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class HttpResponseTestObjects {

  public static HttpResponse<String> httpResponseTestObjectProvider(String testCaseCode) {
    final int statusCode;
    final String fileName;
    switch (testCaseCode) {
      case "tc_001":
        statusCode = 200;
        fileName = "src/test/resources/completeCanaryResponse.json";
        break;
      case "tc_002":
        statusCode = 400;
        fileName = "src/test/resources/status400_1.json";
        break;
      case "tc_003":
        statusCode = 202;
        fileName = "src/test/resources/status202.json";
        break;
      case "tc_004":
        statusCode = 401;
        fileName = "src/test/resources/status401.json";
        break;
      case "tc_005":
        statusCode = 404;
        fileName = "src/test/resources/status404.json";
        break;
      case "tc_006":
        statusCode = 403;
        fileName = "src/test/resources/status403.json";
        break;
      case "tc_007":
        statusCode = 422;
        fileName = "src/test/resources/status422.json";
        break;
      case "tc_051":
        statusCode = 200;
        fileName = "src/test/resources/companyEnrichResponse.json";
        break;
      case "tc_071":
        statusCode = 200;
        fileName = "src/test/resources/companySearchResponse.json";
        break;
      default:
        statusCode = 500;
        fileName = "";
    }

    return new HttpResponse<>() {
      @Override
      public int statusCode() {
        return statusCode;
      }

      @Override
      public HttpRequest request() {
        return null;
      }

      @Override
      public Optional<HttpResponse<String>> previousResponse() {
        return Optional.empty();
      }

      @Override
      public HttpHeaders headers() {
        return null;
      }

      @Override
      public String body() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
          String s;
          StringBuilder sb = new StringBuilder();
          while ((s = br.readLine()) != null) {
            sb.append(s.trim());
          }
          return sb.toString();
        } catch (IOException e) {
        }
        return null;
      }

      @Override
      public Optional<SSLSession> sslSession() {
        return Optional.empty();
      }

      @Override
      public URI uri() {
        return null;
      }

      @Override
      public HttpClient.Version version() {
        return null;
      }
    };
  }
}
