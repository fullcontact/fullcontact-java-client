package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.auth.CredentialsProvider;
import com.fullcontact.apilib.auth.StaticApiKeyCredentialProvider;
import com.fullcontact.apilib.models.Profile;
import com.fullcontact.apilib.models.Request.ResolveRequest;
import com.fullcontact.apilib.models.Response.ResolveResponse;
import com.fullcontact.apilib.models.Response.ResolveResponseWithTags;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class ResolveResponseTest {
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
  public void resolveResponseModelDeserializationTest1()
      throws FullContactException, ExecutionException, InterruptedException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_101");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest().email("marquitaross006@gmail.com").build();

    ResolveResponse response = fcTest.identityMap(resolveRequest).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertEquals("21c300bcf16b079ae52025cc1c06765c", response.getRecordIds().get(0));
    Assert.assertEquals("key", response.getTags().get(0).getKey());
  }

  @Test
  public void resolveResponseModelDeserializationTest2()
      throws FullContactException, ExecutionException, InterruptedException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_102");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest().email("test").recordId("customer123").build();

    ResolveResponse response = fcTest.identityResolve(resolveRequest).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertEquals("customer123", response.getRecordIds().get(0));
    Assert.assertEquals(
        "VS1OPPPPvxHcCNPezUbvYBCDEAOdSj5AI0adsA2bLmh12345", response.getPersonIds().get(0));
  }

  @Test
  public void resolveResponseModelDeserializationTest3()
      throws FullContactException, ExecutionException, InterruptedException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_102");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest().email("test").recordId("customer123").build();

    ResolveResponse response = fcTest.identityMapResolve(resolveRequest).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertEquals("customer123", response.getRecordIds().get(0));
    Assert.assertEquals(
        "VS1OPPPPvxHcCNPezUbvYBCDEAOdSj5AI0adsA2bLmh12345", response.getPersonIds().get(0));
  }

  @Test
  public void identityDeleteTest()
      throws FullContactException, ExecutionException, InterruptedException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_103");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest().phone("test").recordId("customer123").build();

    ResolveResponse response = fcTest.identityDelete(resolveRequest).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(204, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertNull(response.getRecordIds());
    Assert.assertNull(response.getPersonIds());
  }

  @Test
  public void identityMapWithCustomRetryHandlerTest()
      throws FullContactException, ExecutionException, InterruptedException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_101");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest().email("marquitaross006@gmail.com").build();

    ResolveResponse response = fcTest.identityMap(resolveRequest, new CustomRetryHandler()).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertEquals("21c300bcf16b079ae52025cc1c06765c", response.getRecordIds().get(0));
  }

  @Test
  public void identityResolveWithCustomRetryHandlerTest()
      throws FullContactException, ExecutionException, InterruptedException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_102");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest()
            .profile(Profile.builder().url("test").build())
            .recordId("customer123")
            .build();

    ResolveResponse response = fcTest.identityMap(resolveRequest, new CustomRetryHandler()).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertEquals("customer123", response.getRecordIds().get(0));
    Assert.assertEquals(
        "VS1OPPPPvxHcCNPezUbvYBCDEAOdSj5AI0adsA2bLmh12345", response.getPersonIds().get(0));
  }

  @Test
  public void identityDeleteWithCustomRetryHandlerTest()
      throws FullContactException, ExecutionException, InterruptedException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_103");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest().email("test").recordId("customer123").build();

    ResolveResponse response = fcTest.identityMap(resolveRequest, new CustomRetryHandler()).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(204, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertNull(response.getRecordIds());
    Assert.assertNull(response.getPersonIds());
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

    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest().email("marquitaross006@gmail.com").build();
    ResolveResponse response = fcTest.identityMap(resolveRequest).get();
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

    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest().email("marquitaross006@gmail.com").build();
    ResolveResponse response = fcTest.identityMap(resolveRequest).get();
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

    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest().email("martesttyh97@gmail.com").build();
    ResolveResponse response = fcTest.identityResolve(resolveRequest).get();
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

    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest().email("marte7@gmail.com").recordId("test").build();
    ResolveResponse response = fcTest.identityDelete(resolveRequest).get();
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

    ResolveRequest resolveRequest = FullContact.buildResolveRequest().email("marte7@gmail").build();
    ResolveResponse response = fcTest.identityResolve(resolveRequest).get();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(422, response.getStatusCode());
    Assert.assertTrue(
        response
            .getMessage()
            .contains("Input domain parameter (\"fullcontact\") does not contain a valid domain."));
  }

  @Test
  public void resolveResponseWithTagsTest()
      throws FullContactException, ExecutionException, InterruptedException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_104");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest().email("test").recordId("customer123").build();

    ResolveResponseWithTags response = fcTest.identityResolveWithTags(resolveRequest).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertEquals("customer123", response.getRecordIds().get(0));
    Assert.assertEquals(
        "VS1OPPPPvxHcCNPezUbvYBCDEAOdSj5AI0adsA2bLmh12345", response.getPersonIds().get(0));
    Assert.assertEquals("gender", response.getTags().get("customer123").get(0).getKey());
  }
}
