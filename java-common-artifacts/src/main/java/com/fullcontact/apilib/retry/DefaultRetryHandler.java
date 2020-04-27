package com.fullcontact.apilib.retry;

public class DefaultRetryHandler implements RetryHandler {
  @Override
  public boolean shouldRetry(int responseCode) {
    return (responseCode == 429 || responseCode == 503);
  }

  @Override
  public int getRetryAttempts() {
    return 1;
  }

  @Override
  public int getRetryDelayMillis() {
    return 1000;
  }
}
