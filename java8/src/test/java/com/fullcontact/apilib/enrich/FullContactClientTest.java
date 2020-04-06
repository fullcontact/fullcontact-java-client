package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.auth.StaticApiKeyCredentialProvider;
import org.junit.*;

import java.util.HashMap;

public class FullContactClientTest {

  @Test
  public void buildClientWithoutKeyTest() {
    HashMap<String, String> customHeader = new HashMap<>();
    customHeader.put("Reporting-Key", "clientXYZ");
    try {
      FullContact fcTest =
          FullContact.builder()
              .retryDelayMillis(2000)
              .retryAttempts(7)
              .userAgent("user-Agent")
              .headers(customHeader)
              .build();
      fcTest.close();
    } catch (FullContactException fce) {
      Assert.assertEquals(
          "Couldn't find valid API Key from ENV variable: FC_API_KEY", fce.getMessage());
    }
  }

  @Test
  public void buildClientWithStaticKeyTest() throws FullContactException {
    HashMap<String, String> customHeader = new HashMap<>();
    customHeader.put("Reporting-Key", "clientXYZ");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(new StaticApiKeyCredentialProvider("test-api-key"))
            .retryDelayMillis(2000)
            .retryAttempts(7)
            .userAgent("user-Agent")
            .headers(customHeader)
            .build();
    fcTest.close();
  }

  @Test
  public void emptyStaticKeyTest() {
    try {

      FullContact fcTest =
          FullContact.builder().credentialsProvider(new StaticApiKeyCredentialProvider("")).build();
      fcTest.close();
    } catch (FullContactException e) {
      Assert.assertEquals("API Key can't be Empty", e.getMessage());
    }
  }

  @Test
  public void nullStaticKeyTest() {
    try {

      FullContact fcTest =
          FullContact.builder()
              .credentialsProvider(new StaticApiKeyCredentialProvider(null))
              .build();
      fcTest.close();

    } catch (FullContactException e) {
      Assert.assertEquals("API Key can't be Empty", e.getMessage());
    }
  }
}
