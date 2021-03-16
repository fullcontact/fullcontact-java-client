package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.auth.CredentialsProvider;
import com.fullcontact.apilib.auth.StaticApiKeyCredentialProvider;
import com.fullcontact.apilib.models.Request.PermissionRequest;
import com.fullcontact.apilib.models.Request.PurposeRequest;
import com.fullcontact.apilib.models.Response.FCResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class FCResponseTest {
  private static HashMap<String, String> customHeader = new HashMap<>();

  @Before
  public void init() {
    System.setProperty("FC_TEST_ENV", "FC_TEST");
  }

  @After
  public void reset() {
    System.clearProperty("FC_TEST_ENV");
  }

  // Permission Create Response Test - Start
  @Test
  public void personResponseModelDeserializationTest()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_501");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .consentPurpose(PurposeRequest.builder().purposeId(1).enabled(true).build())
            .query(FullContact.buildMultifieldRequest().email("test@fullcontact.com").build())
            .policyUrl("test")
            .termsService("test")
            .ipAddress("test")
            .collectionLocation("test")
            .collectionMethod("test")
            .build();
    FCResponse response = fcTest.permissionCreate(permissionRequest).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(202, response.getStatusCode());
    Assert.assertEquals("Accepted", response.getMessage());
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

    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .consentPurpose(PurposeRequest.builder().purposeId(1).enabled(true).build())
            .query(FullContact.buildMultifieldRequest().email("test@fullcontact.com").build())
            .policyUrl("test")
            .termsService("test")
            .ipAddress("test")
            .collectionLocation("test")
            .collectionMethod("test")
            .build();
    FCResponse response = fcTest.permissionCreate(permissionRequest).get();
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

    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .consentPurpose(PurposeRequest.builder().purposeId(1).enabled(true).build())
            .query(FullContact.buildMultifieldRequest().email("test@fullcontact.com").build())
            .policyUrl("test")
            .termsService("test")
            .ipAddress("test")
            .collectionLocation("test")
            .collectionMethod("test")
            .build();
    FCResponse response = fcTest.permissionCreate(permissionRequest).get();
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

    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .consentPurpose(PurposeRequest.builder().purposeId(1).enabled(true).build())
            .query(FullContact.buildMultifieldRequest().email("test@fullcontact.com").build())
            .policyUrl("test")
            .termsService("test")
            .ipAddress("test")
            .collectionLocation("test")
            .collectionMethod("test")
            .build();
    FCResponse response = fcTest.permissionCreate(permissionRequest).get();
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

    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .consentPurpose(PurposeRequest.builder().purposeId(1).enabled(true).build())
            .query(FullContact.buildMultifieldRequest().email("test@fullcontact.com").build())
            .policyUrl("test")
            .termsService("test")
            .ipAddress("test")
            .collectionLocation("test")
            .collectionMethod("test")
            .build();
    FCResponse response = fcTest.permissionCreate(permissionRequest).get();
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

    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .consentPurpose(PurposeRequest.builder().purposeId(1).enabled(true).build())
            .query(FullContact.buildMultifieldRequest().email("test@fullcontact.com").build())
            .policyUrl("test")
            .termsService("test")
            .ipAddress("test")
            .collectionLocation("test")
            .collectionMethod("test")
            .build();
    FCResponse response = fcTest.permissionCreate(permissionRequest).get();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(422, response.getStatusCode());
    Assert.assertTrue(
        response
            .getMessage()
            .contains("Input domain parameter (\"fullcontact\") does not contain a valid domain."));
  }

  // Permission Create Response Test - End

  // Permission Delete Response Test - Start

  @Test
  public void personDeleteResponseModelDeserializationTest()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_501");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();
    FCResponse response =
        fcTest
            .permissionDelete(
                FullContact.buildMultifieldRequest().email("test@fullcontact.com").build())
            .get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(202, response.getStatusCode());
    Assert.assertEquals("Accepted", response.getMessage());
  }

  @Test
  public void permissionDeleteResponseStatus400Test()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_002");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    FCResponse response =
        fcTest
            .permissionDelete(
                FullContact.buildMultifieldRequest().email("test@fullcontact.com").build())
            .get();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(400, response.getStatusCode());
    Assert.assertEquals("Unable to process JSON", response.getMessage());
  }

  @Test
  public void permissionDeleteResponseStatus401Test()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_004");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    FCResponse response =
        fcTest
            .permissionDelete(
                FullContact.buildMultifieldRequest().email("test@fullcontact.com").build())
            .get();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(401, response.getStatusCode());
    Assert.assertTrue(
        response.getMessage().contains("401: Invalid access token: 0kj39la0c309cnw90"));
  }

  @Test
  public void permissionDeleteResponseStatus404Test()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_005");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    FCResponse response =
        fcTest
            .permissionDelete(
                FullContact.buildMultifieldRequest().email("test@fullcontact.com").build())
            .get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(404, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("Profile not found"));
  }

  @Test
  public void permissionDeleteResponseStatus403Test()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_006");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    FCResponse response =
        fcTest
            .permissionDelete(
                FullContact.buildMultifieldRequest().email("test@fullcontact.com").build())
            .get();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(403, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("API Key is missing or invalid."));
  }

  @Test
  public void permissionDeleteResponseStatus422Test()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_007");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    FCResponse response =
        fcTest
            .permissionDelete(
                FullContact.buildMultifieldRequest().email("test@fullcontact.com").build())
            .get();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(422, response.getStatusCode());
    Assert.assertTrue(
        response
            .getMessage()
            .contains("Input domain parameter (\"fullcontact\") does not contain a valid domain."));
  }

  // Permission Delete Response Test - End
}
