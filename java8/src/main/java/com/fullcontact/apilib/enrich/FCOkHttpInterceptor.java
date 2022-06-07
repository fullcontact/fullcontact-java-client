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
    return chain.proceed(originalRequest);
  }
}
