package com.fullcontact.apilib.retry;

public interface RetryHandler {

  /**
   * Specifies retry condition based on status code of response
   *
   * @param responseCode Response status code
   * @return boolean condition for retry
   */
  boolean shouldRetry(int responseCode);

  /**
   * Specifies Number of Retry Attempts allowed
   *
   * @return int: Number of retry attempts
   */
  int getRetryAttempts();

  /**
   * Specifies Delay time in milliseconds before each retry
   *
   * @return int: Delay time in milliseconds
   */
  int getRetryDelayMillis();
}
