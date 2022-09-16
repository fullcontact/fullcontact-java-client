package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.auth.CredentialsProvider;
import com.fullcontact.apilib.auth.StaticApiKeyCredentialProvider;
import com.fullcontact.apilib.models.Request.AudienceRequest;
import com.fullcontact.apilib.models.Response.AudienceResponse;
import com.fullcontact.apilib.models.Tag;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AudienceResponseTest {
  private static HashMap<String, String> customHeader = new HashMap<>();

  @Before
  public void init() {
    System.setProperty("FC_TEST_ENV", "FC_TEST");
  }

  @After
  public void reset() {
    System.clearProperty("FC_TEST_ENV");
  }

  @Test
  public void AudienceResponseModelDeserializationTest1()
      throws FullContactException, ExecutionException, InterruptedException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_401");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    AudienceRequest audienceRequest =
        FullContact.buildAudienceRequest()
            .webhookUrl("webhookUrl")
            .tag(Tag.builder().key("gender").value("female").build())
            .build();

    AudienceResponse response = fcTest.audienceCreate(audienceRequest).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertEquals("72e53890-17b5-o49d-a651-58we0c7980d3", response.getRequestId());
  }

  @Test
  public void AudienceResponseModelDeserializationTest2()
      throws FullContactException, ExecutionException, InterruptedException, IOException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_402");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();
    String requestId = "72e53890-17b5-o49d-a651-58we0c7980d3";

    AudienceResponse response = fcTest.audienceDownload(requestId).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    // Uncomment below to test audience file generation
    // response.getFileFromBytes(requestId);
  }

  @Test
  public void responseStatus400Test()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_002");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    AudienceRequest audienceRequest =
        FullContact.buildAudienceRequest()
            .webhookUrl("webhookUrl")
            .tag(Tag.builder().key("gender").value("female").build())
            .build();

    AudienceResponse response = fcTest.audienceCreate(audienceRequest).get();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(400, response.getStatusCode());
    Assert.assertEquals("Unable to process JSON", response.getMessage());
  }

  @Test
  public void responseStatus401Test()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_004");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    AudienceRequest audienceRequest =
        FullContact.buildAudienceRequest()
            .webhookUrl("webhookUrl")
            .tag(Tag.builder().key("gender").value("female").build())
            .build();

    AudienceResponse response = fcTest.audienceCreate(audienceRequest).get();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(401, response.getStatusCode());
    Assert.assertTrue(
        response.getMessage().contains("401: Invalid access token: 0kj39la0c309cnw90"));
  }

  @Test
  public void responseStatus404Test()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_005");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    AudienceRequest audienceRequest =
        FullContact.buildAudienceRequest()
            .webhookUrl("webhookUrl")
            .tag(Tag.builder().key("gender").value("female").build())
            .build();

    AudienceResponse response = fcTest.audienceCreate(audienceRequest).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(404, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("Profile not found"));
  }

  @Test
  public void responseStatus403Test()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_006");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    AudienceRequest audienceRequest =
        FullContact.buildAudienceRequest()
            .webhookUrl("webhookUrl")
            .tag(Tag.builder().key("gender").value("female").build())
            .build();

    AudienceResponse response = fcTest.audienceCreate(audienceRequest).get();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(403, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("API Key is missing or invalid."));
  }

  @Test
  public void responseStatus422Test()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_007");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    AudienceRequest audienceRequest =
        FullContact.buildAudienceRequest()
            .webhookUrl("webhookUrl")
            .tag(Tag.builder().key("gender").value("female").build())
            .build();

    AudienceResponse response = fcTest.audienceCreate(audienceRequest).get();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(422, response.getStatusCode());
    Assert.assertTrue(
        response
            .getMessage()
            .contains("Input domain parameter (\"fullcontact\") does not contain a valid domain."));
  }
}
