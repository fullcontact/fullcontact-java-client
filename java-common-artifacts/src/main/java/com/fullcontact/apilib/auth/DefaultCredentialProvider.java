package com.fullcontact.apilib.auth;

import static com.fullcontact.apilib.auth.CredentialConstants.*;

import com.fullcontact.apilib.FullContactException;
import lombok.Getter;

/** Default way of providing authentication through Environment variables */
@Getter
public class DefaultCredentialProvider implements CredentialsProvider {
  private final String apiKey;

  /**
   * Default constructor looks for API key at environment variable 'FC_API_KEY'
   *
   * @throws FullContactException Exception if key is not found or is empty
   */
  public DefaultCredentialProvider() throws FullContactException {
    String envApiKey = System.getenv(FC_API_KEY);
    if (envApiKey != null && !envApiKey.trim().isEmpty()) {
      this.apiKey = envApiKey;
    } else {
      throw new FullContactException(
          "Couldn't find valid API Key from ENV variable: " + FC_API_KEY);
    }
  }

  /**
   * Parameterized constructor to provide a different environment variable name for API key
   *
   * @param envVar Environment variable where API key is stored
   * @throws FullContactException Exception if key is not found or is empty
   */
  public DefaultCredentialProvider(String envVar) throws FullContactException {
    String envApiKey = System.getenv(envVar);
    if (envApiKey != null && !envApiKey.trim().isEmpty()) {
      this.apiKey = envApiKey;
    } else {
      throw new FullContactException("Couldn't find valid API Key from ENV variable: " + envVar);
    }
  }
}
