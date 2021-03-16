package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.auth.CredentialsProvider;
import com.fullcontact.apilib.auth.StaticApiKeyCredentialProvider;
import com.fullcontact.apilib.models.Request.ChannelPurposeRequest;
import com.fullcontact.apilib.models.Request.MultifieldRequest;
import com.fullcontact.apilib.models.Response.ConsentPurposeResponse;
import com.fullcontact.apilib.models.Response.PermissionCurrentResponseMap;
import com.fullcontact.apilib.models.Response.PermissionResponseList;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class PermissionResponseTest {
  private static HashMap<String, String> customHeader = new HashMap<>();

  @Before
  public void init() {
    System.setProperty("FC_TEST_ENV", "FC_TEST");
  }

  @After
  public void reset() {
    System.clearProperty("FC_TEST_ENV");
  }
  // Permission Find Response Test - Start

  @Test
  public void permissionResponseListModelDeserializationTest()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_502");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();
    MultifieldRequest query =
        FullContact.buildMultifieldRequest().email("test@fullcontact.com").build();
    PermissionResponseList response = fcTest.permissionFind(query).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("create", response.permissionResponseList.get(0).getPermissionType());
    Assert.assertEquals(
        "web", response.permissionResponseList.get(0).getConsentPurposes().get(0).getChannel());
    Assert.assertEquals(
        6, response.permissionResponseList.get(0).getConsentPurposes().get(0).getPurposeId());
    Assert.assertEquals("cookie", response.permissionResponseList.get(0).getCollectionMethod());
    Assert.assertEquals(
        "web", response.permissionResponseList.get(1).getConsentPurposes().get(0).getChannel());
    Assert.assertTrue(
        response.permissionResponseList.get(0).getConsentPurposes().get(0).isEnabled());
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

    MultifieldRequest query =
        FullContact.buildMultifieldRequest().email("test@fullcontact.com").build();
    PermissionResponseList response = fcTest.permissionFind(query).get();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(400, response.getStatusCode());
    Assert.assertEquals("Unable to process JSON", response.getMessage());
  }

  @Test
  public void responseStatus202Test()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_003");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    MultifieldRequest query =
        FullContact.buildMultifieldRequest().email("test@fullcontact.com").build();
    PermissionResponseList response = fcTest.permissionFind(query).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(202, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("Queued for search"));
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

    MultifieldRequest query =
        FullContact.buildMultifieldRequest().email("test@fullcontact.com").build();
    PermissionResponseList response = fcTest.permissionFind(query).get();
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

    MultifieldRequest query =
        FullContact.buildMultifieldRequest().email("test@fullcontact.com").build();
    PermissionResponseList response = fcTest.permissionFind(query).get();
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

    MultifieldRequest query =
        FullContact.buildMultifieldRequest().email("test@fullcontact.com").build();
    PermissionResponseList response = fcTest.permissionFind(query).get();
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

    MultifieldRequest query =
        FullContact.buildMultifieldRequest().email("test@fullcontact.com").build();
    PermissionResponseList response = fcTest.permissionFind(query).get();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(422, response.getStatusCode());
    Assert.assertTrue(
        response
            .getMessage()
            .contains("Input domain parameter (\"fullcontact\") does not contain a valid domain."));
  }

  // Permission Find Response Test - End

  // Permission Current Response Test - Start
  @Test
  public void permissionCurrentResponseModelDeserializationTest()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_503");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    MultifieldRequest query =
        FullContact.buildMultifieldRequest().email("test@fullcontact.com").build();
    PermissionCurrentResponseMap response = fcTest.permissionCurrent(query).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertTrue(response.getResponseMap().get(3).get("mobile").isEnabled());
    Assert.assertEquals("email", response.getResponseMap().get(3).get("email").getChannel());
    Assert.assertEquals(3, response.getResponseMap().get(3).get("phone").getPurposeId());
    Assert.assertTrue(response.getResponseMap().get(6).get("web").isEnabled());
    Assert.assertEquals("offline", response.getResponseMap().get(10).get("offline").getChannel());
    Assert.assertEquals("phone", response.getResponseMap().get(10).get("phone").getChannel());
  }

  @Test
  public void permissionCurrentResponseStatus400Test()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_002");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    MultifieldRequest query =
        FullContact.buildMultifieldRequest().email("test@fullcontact.com").build();
    PermissionCurrentResponseMap response = fcTest.permissionCurrent(query).get();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(400, response.getStatusCode());
    Assert.assertEquals("Unable to process JSON", response.getMessage());
  }

  @Test
  public void permissionCurrentResponseStatus202Test()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_003");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    MultifieldRequest query =
        FullContact.buildMultifieldRequest().email("test@fullcontact.com").build();
    PermissionCurrentResponseMap response = fcTest.permissionCurrent(query).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(202, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("Queued for search"));
  }

  @Test
  public void permissionCurrentResponseStatus401Test()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_004");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    MultifieldRequest query =
        FullContact.buildMultifieldRequest().email("test@fullcontact.com").build();
    PermissionCurrentResponseMap response = fcTest.permissionCurrent(query).get();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(401, response.getStatusCode());
    Assert.assertTrue(
        response.getMessage().contains("401: Invalid access token: 0kj39la0c309cnw90"));
  }

  @Test
  public void permissionCurrentResponseStatus404Test()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_005");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    MultifieldRequest query =
        FullContact.buildMultifieldRequest().email("test@fullcontact.com").build();
    PermissionCurrentResponseMap response = fcTest.permissionCurrent(query).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(404, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("Profile not found"));
  }

  @Test
  public void permissionCurrentResponseStatus403Test()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_006");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    MultifieldRequest query =
        FullContact.buildMultifieldRequest().email("test@fullcontact.com").build();
    PermissionCurrentResponseMap response = fcTest.permissionCurrent(query).get();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(403, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("API Key is missing or invalid."));
  }

  @Test
  public void permissionCurrentResponseStatus422Test()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_007");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    MultifieldRequest query =
        FullContact.buildMultifieldRequest().email("test@fullcontact.com").build();
    PermissionCurrentResponseMap response = fcTest.permissionCurrent(query).get();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(422, response.getStatusCode());
    Assert.assertTrue(
        response
            .getMessage()
            .contains("Input domain parameter (\"fullcontact\") does not contain a valid domain."));
  }

  // Permission Current Response Test - End

  // Permission Verify Response Test - Start
  @Test
  public void consentPurposeResponseModelDeserializationTest()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_504");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();
    MultifieldRequest query =
        FullContact.buildMultifieldRequest().email("test@fullcontact.com").build();
    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest().query(query).channel("web").purposeId(6).build();
    ConsentPurposeResponse response = fcTest.permissionVerify(channelPurposeRequest).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertEquals(6, response.getPurposeId());
    Assert.assertTrue(response.isEnabled());
    Assert.assertEquals("Content selection, delivery & reporting", response.getPurposeName());
  }

  @Test
  public void consentPurposeResponseStatus400Test()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_002");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    MultifieldRequest query =
        FullContact.buildMultifieldRequest().email("test@fullcontact.com").build();
    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest().query(query).channel("web").purposeId(6).build();
    ConsentPurposeResponse response = fcTest.permissionVerify(channelPurposeRequest).get();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(400, response.getStatusCode());
    Assert.assertEquals("Unable to process JSON", response.getMessage());
  }

  @Test
  public void consentPurposeResponseStatus202Test()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_003");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    MultifieldRequest query =
        FullContact.buildMultifieldRequest().email("test@fullcontact.com").build();
    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest().query(query).channel("web").purposeId(6).build();
    ConsentPurposeResponse response = fcTest.permissionVerify(channelPurposeRequest).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(202, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("Queued for search"));
  }

  @Test
  public void consentPurposeResponseStatus401Test()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_004");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    MultifieldRequest query =
        FullContact.buildMultifieldRequest().email("test@fullcontact.com").build();
    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest().query(query).channel("web").purposeId(6).build();
    ConsentPurposeResponse response = fcTest.permissionVerify(channelPurposeRequest).get();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(401, response.getStatusCode());
    Assert.assertTrue(
        response.getMessage().contains("401: Invalid access token: 0kj39la0c309cnw90"));
  }

  @Test
  public void consentPurposeResponseStatus404Test()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_005");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    MultifieldRequest query =
        FullContact.buildMultifieldRequest().email("test@fullcontact.com").build();
    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest().query(query).channel("web").purposeId(6).build();
    ConsentPurposeResponse response = fcTest.permissionVerify(channelPurposeRequest).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(404, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("Profile not found"));
  }

  @Test
  public void consentPurposeResponseStatus403Test()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_006");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    MultifieldRequest query =
        FullContact.buildMultifieldRequest().email("test@fullcontact.com").build();
    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest().query(query).channel("web").purposeId(6).build();
    ConsentPurposeResponse response = fcTest.permissionVerify(channelPurposeRequest).get();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(403, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("API Key is missing or invalid."));
  }

  @Test
  public void consentPurposeResponseStatus422Test()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_007");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    MultifieldRequest query =
        FullContact.buildMultifieldRequest().email("test@fullcontact.com").build();
    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest().query(query).channel("web").purposeId(6).build();
    ConsentPurposeResponse response = fcTest.permissionVerify(channelPurposeRequest).get();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(422, response.getStatusCode());
    Assert.assertTrue(
        response
            .getMessage()
            .contains("Input domain parameter (\"fullcontact\") does not contain a valid domain."));
  }

  // Permission Verify Response Test - End
}
