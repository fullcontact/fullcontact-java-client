package com.fullcontact.apilib.auth;

/** CredentialsProvider interface provides method for authentication */
public interface CredentialsProvider {
  /**
   * Returns a API key that can be used for authentication
   *
   * @return String API key
   */
  String getApiKey();
}
