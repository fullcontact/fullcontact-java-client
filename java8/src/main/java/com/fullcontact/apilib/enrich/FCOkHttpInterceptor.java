package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.FCConstants;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class FCOkHttpInterceptor implements Interceptor {
  @Override
  public Response intercept(Chain chain) throws IOException {
    Request originalRequest = chain.request();
    if (!originalRequest.url().toString().contains("verification/email")) {
      return chain.proceed(originalRequest);
    }
    String originalUrl = originalRequest.url().toString();
    HttpUrl newUrl =
        HttpUrl.parse(originalUrl.replace(FCConstants.API_BASE_DEFAULT, FCConstants.API_BASE_V2));
    if (newUrl == null) {
      return chain.proceed(originalRequest);
    }
    Request newRequest = originalRequest.newBuilder().url(newUrl).build();
    return chain.proceed(newRequest);
  }
}
