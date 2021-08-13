package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.auth.StaticApiKeyCredentialProvider;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;

public class FullContactClientTest {
  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void buildClientWithoutKeyTest() throws FullContactException {
    HashMap<String, String> customHeader = new HashMap<>();
    customHeader.put("Reporting-Key", "clientXYZ");
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage("Couldn't find valid API Key from ENV variable: FC_API_KEY");
    FullContact fcTest = FullContact.builder().headers(customHeader).build();
    fcTest.close();
  }

  @Test
  public void buildClientWithStaticKeyTest() throws FullContactException {
    HashMap<String, String> customHeader = new HashMap<>();
    customHeader.put("Reporting-Key", "clientXYZ");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(new StaticApiKeyCredentialProvider("test-api-key"))
            .headers(customHeader)
            .build();
    fcTest.close();
  }

  @Test
  public void emptyStaticKeyTest() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage("API Key can't be Empty");
    FullContact fcTest =
        FullContact.builder().credentialsProvider(new StaticApiKeyCredentialProvider("")).build();
    fcTest.close();
  }

  @Test
  public void nullStaticKeyTest() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage("API Key can't be Empty");
    FullContact fcTest =
        FullContact.builder().credentialsProvider(new StaticApiKeyCredentialProvider(null)).build();
    fcTest.close();
  }

  @Test
  public void customRetryHandlerClientTest() throws FullContactException {
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(new StaticApiKeyCredentialProvider("api-key"))
            .connectTimeoutMillis(2000)
            .retryHandler(new CustomRetryHandler())
            .build();
    fcTest.close();
  }
}
