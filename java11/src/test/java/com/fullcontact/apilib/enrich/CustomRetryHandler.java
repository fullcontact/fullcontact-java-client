package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.retry.RetryHandler;

public class CustomRetryHandler implements RetryHandler {
  @Override
  public boolean shouldRetry(int responseCode) {
    return responseCode == 429;
  }

  @Override
  public int getRetryAttempts() {
    return 4;
  }

  @Override
  public int getRetryDelayMillis() {
    return 4000;
  }
}
