package com.fullcontact.apilib.auth;

import com.fullcontact.apilib.FullContactException;
import lombok.Getter;

/** Static way of providing authentication */
@Getter
public class StaticApiKeyCredentialProvider implements CredentialsProvider {
  private final String apiKey;

  /**
   * Hardcoded key can be passed in the constructor
   *
   * @param apiKey API key for auth
   * @throws FullContactException if key is null or empty
   */
  public StaticApiKeyCredentialProvider(String apiKey) throws FullContactException {
    if (apiKey == null || apiKey.trim().isEmpty()) {
      throw new FullContactException("API Key can't be Empty");
    }
    this.apiKey = apiKey;
  }
}
