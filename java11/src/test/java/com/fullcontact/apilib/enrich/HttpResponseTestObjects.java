package com.fullcontact.apilib.enrich;

import javax.net.ssl.SSLSession;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.zip.GZIPOutputStream;

public class HttpResponseTestObjects {

  public static byte[] compress(String data) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
    GZIPOutputStream gzip = new GZIPOutputStream(bos);
    gzip.write(data.getBytes());
    gzip.close();
    byte[] compressed = bos.toByteArray();
    bos.close();
    return compressed;
  }

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
      case "tc_101":
        statusCode = 200;
        fileName = "src/test/resources/identityMapResponse.json";
        break;
      case "tc_102":
        statusCode = 200;
        fileName = "src/test/resources/identityResolveResponse.json";
        break;
      case "tc_103":
      case "tc_303":
        statusCode = 204;
        fileName = "src/test/resources/empty.json";
        break;
      case "tc_104":
        statusCode = 200;
        fileName = "src/test/resources/identityResolveResponseWithTags.json";
        break;
      case "tc_301":
        statusCode = 200;
        fileName = "src/test/resources/tagsCreateResponse.json";
        break;
      case "tc_302":
        statusCode = 200;
        fileName = "src/test/resources/tagsGetResponse.json";
        break;
      case "tc_401":
        statusCode = 200;
        fileName = "src/test/resources/audienceCreateResponse.json";
        break;
      case "tc_501":
        statusCode = 202;
        fileName = null;
        break;
      case "tc_502":
        statusCode = 200;
        fileName = "src/test/resources/permissionResponseList.json";
        break;
      case "tc_503":
        statusCode = 200;
        fileName = "src/test/resources/permissionCurrentResponse.json";
        break;
      case "tc_504":
        statusCode = 200;
        fileName = "src/test/resources/permissionVerifyResponse.json";
        break;
      case "tc_601":
        statusCode = 200;
        fileName = "src/test/resources/verifySignalsResponse.json";
        break;
      case "tc_602":
        statusCode = 200;
        fileName = "src/test/resources/verifyActivityResponse.json";
        break;
      case "tc_603":
        statusCode = 200;
        fileName = "src/test/resources/verifyMatchResponse.json";
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
        if (fileName == null) {
          return null;
        }
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

  public static HttpResponse<byte[]> httpByteResponseTestObjectProvider(String testCaseCode) {
    final int statusCode;
    final String fileName;
    switch (testCaseCode) {
      case "tc_402":
        statusCode = 200;
        fileName = "src/test/resources/audienceDownloadResponse.json";
        break;
      default:
        statusCode = 500;
        fileName = "";
    }
    return new HttpResponse<byte[]>() {
      @Override
      public int statusCode() {
        return statusCode;
      }

      @Override
      public HttpRequest request() {
        return null;
      }

      @Override
      public Optional<HttpResponse<byte[]>> previousResponse() {
        return Optional.empty();
      }

      @Override
      public HttpHeaders headers() {
        return null;
      }

      @Override
      public byte[] body() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
          String s;
          StringBuilder sb = new StringBuilder();
          while ((s = br.readLine()) != null) {
            sb.append(s.trim());
          }
          return compress(sb.toString());
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
