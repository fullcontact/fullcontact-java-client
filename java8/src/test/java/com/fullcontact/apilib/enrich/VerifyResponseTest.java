package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.auth.CredentialsProvider;
import com.fullcontact.apilib.auth.StaticApiKeyCredentialProvider;
import com.fullcontact.apilib.models.Location;
import com.fullcontact.apilib.models.PersonName;
import com.fullcontact.apilib.models.Request.MultifieldRequest;
import com.fullcontact.apilib.models.Response.ActivityResponse;
import com.fullcontact.apilib.models.Response.MatchResponse;
import com.fullcontact.apilib.models.Response.SignalsResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class VerifyResponseTest {
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
  public void signalsResponseModelDeserializationTest()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_601");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    MultifieldRequest multifieldRequest =
        FullContact.buildMultifieldRequest().email("chipshipperman@gmail.com").build();
    SignalsResponse response = fcTest.verifySignals(multifieldRequest).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertEquals(4, response.getEmails().size());
    Assert.assertEquals("abcd8663403ca772e503ffa53fab5773", response.getEmails().get(0).getMd5());
    Assert.assertEquals(
        "abc34fb5660e082d5245968e4feb9a726d2554b7", response.getEmails().get(0).getSha1());
    Assert.assertEquals(
        "abc5bbfb3e3c05a83cb23db4daaf3da6a255251fd22ca3068d121d26d66587e0",
        response.getEmails().get(0).getSha256());
    Assert.assertEquals(207, response.getEmails().get(0).getObservations());
    Assert.assertEquals("1.0", String.valueOf(response.getEmails().get(0).getConfidence()));
    Assert.assertEquals("Chip", response.getName().getGiven());
    Assert.assertEquals("Shipperman", response.getName().getFamily());
    Assert.assertEquals(
        "https://twitter.com/ChipShipperman", response.getSocialProfiles().getTwitterUrl());
  }

  @Test
  public void activityResponseModelDeserializationTest()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_602");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    MultifieldRequest multifieldRequest =
        FullContact.buildMultifieldRequest().email("chipshipperman@gmail.com").build();
    ActivityResponse response = fcTest.verifyActivity(multifieldRequest).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertEquals("0.61", String.valueOf(response.getEmails()));
  }

  @Test
  public void matchResponseModelDeserializationTest()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_603");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    MultifieldRequest multifieldRequest =
        FullContact.buildMultifieldRequest()
            .email("chipshipperman@gmail.com")
            .maid("abc75a9f-86f8-4f4e-b6f9-a778b1269ab2")
            .location(
                Location.builder()
                    .addressLine1("1234 Main Street")
                    .city("Stafford")
                    .region("Staffordshire, United Kingdom")
                    .regionCode("United States")
                    .build())
            .name(PersonName.builder().given("Chip").family("Shipperman").build())
            .build();
    MatchResponse response = fcTest.verifyMatch(multifieldRequest).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertFalse(response.getEmail());
    Assert.assertFalse(response.getMaid());
    Assert.assertTrue(response.getGivenName());
    Assert.assertTrue(response.getFamilyName());
    Assert.assertTrue(response.getContinent());
    Assert.assertTrue(response.getPostalCode());
    Assert.assertTrue(response.getCountry());
    Assert.assertTrue(response.getCity());
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

    MultifieldRequest multifieldRequest =
        FullContact.buildMultifieldRequest().email("chipshipperman@gmail.com").build();
    SignalsResponse response = fcTest.verifySignals(multifieldRequest).get();
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

    MultifieldRequest multifieldRequest =
        FullContact.buildMultifieldRequest().email("chipshipperman@gmail.com").build();
    SignalsResponse response = fcTest.verifySignals(multifieldRequest).get();
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

    MultifieldRequest multifieldRequest =
        FullContact.buildMultifieldRequest().email("chipshipperman@gmail.com").build();
    SignalsResponse response = fcTest.verifySignals(multifieldRequest).get();
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

    MultifieldRequest multifieldRequest =
        FullContact.buildMultifieldRequest().email("chipshipperman@gmail.com").build();
    SignalsResponse response = fcTest.verifySignals(multifieldRequest).get();
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

    MultifieldRequest multifieldRequest =
        FullContact.buildMultifieldRequest().email("chipshipperman@gmail.com").build();
    SignalsResponse response = fcTest.verifySignals(multifieldRequest).get();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(422, response.getStatusCode());
    Assert.assertTrue(
        response
            .getMessage()
            .contains("Input domain parameter (\"fullcontact\") does not contain a valid domain."));
  }
}
