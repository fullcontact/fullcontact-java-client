package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.auth.CredentialsProvider;
import com.fullcontact.apilib.auth.StaticApiKeyCredentialProvider;
import com.fullcontact.apilib.models.Request.CompanyRequest;
import com.fullcontact.apilib.models.Response.CompanyResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class CompanyEnrichResponse {
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
  public void companyResponseModelDeserializationTest()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_051");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();
    CompanyRequest companyRequest =
        FullContact.buildCompanyRequest().domain("fullcontact.com").build();
    CompanyResponse response = fcTest.enrich(companyRequest).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertEquals("FullContact Inc.", response.getName());
    Assert.assertEquals("1755 Blake Street Suite 450 Denver CO, 80202 USA", response.getLocation());
    Assert.assertEquals("https://twitter.com/fullcontact", response.getTwitter());
    Assert.assertEquals(
        "https://www.linkedin.com/company/fullcontact-inc-", response.getLinkedin());
    Assert.assertEquals(
        "FullContact is the most powerful fully-connected contact management platform for professionals and enterprises who need to master their contacts and be awesome with people.",
        response.getBio());
    Assert.assertEquals("https://www.fullcontact.com", response.getWebsite());
    Assert.assertEquals(2010, response.getFounded());
    Assert.assertEquals(351, response.getEmployees());
    Assert.assertEquals("English", response.getDetails().getLocales().get(0).getName());
    Assert.assertEquals("3577", response.getDetails().getIndustries().get(0).getCode());
    Assert.assertEquals(
        "team@fullcontact.com", response.getDetails().getEmails().get(1).getValue());
    Assert.assertEquals("+1-888-330-6943", response.getDetails().getPhones().get(2).getValue());
    Assert.assertEquals(
        "https://youtube.com/user/FullContactAPI",
        response.getDetails().getProfiles().getYoutube().getUrl());
    Assert.assertEquals("Denver", response.getDetails().getLocations().get(0).getCity());
    Assert.assertEquals(
        "https://img.fullcontact.com/static/2ab4d453f220d5d33558a29b95d5ef28_b151428e2f8f7f87ca0b7f870eb1799c23598700baab75c45cfb8de2810cf30f",
        response.getDetails().getImages().get(2).getValue());
    Assert.assertEquals(
        "https://www.fullcontact.com", response.getDetails().getUrls().get(0).getValue());
    Assert.assertEquals("Contact Management", response.getDetails().getKeywords().get(1));
    Assert.assertEquals(
        88991, response.getDetails().getTraffic().getCountryRank().getGlobal().getRank());
    Assert.assertEquals(
        24385, response.getDetails().getTraffic().getLocaleRank().getUs().getRank());
    Assert.assertEquals(
        "http://docs.fullcontact.com/api/#key-people",
        response.getDataAddOns().get(0).getDocLink());
  }

  @Test
  public void companyResponseCustomRetryHandlerTest()
      throws FullContactException, InterruptedException, ExecutionException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_051");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();
    CompanyRequest companyRequest =
        FullContact.buildCompanyRequest().domain("fullcontact.com").build();
    CompanyResponse response = fcTest.enrich(companyRequest, new CustomRetryHandler()).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertEquals("FullContact Inc.", response.getName());
    Assert.assertEquals("1755 Blake Street Suite 450 Denver CO, 80202 USA", response.getLocation());
    Assert.assertEquals("https://twitter.com/fullcontact", response.getTwitter());
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

    CompanyRequest companyRequest =
        FullContact.buildCompanyRequest().domain("fullcontact.com").build();
    CompanyResponse response = fcTest.enrich(companyRequest).get();
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

    CompanyRequest companyRequest =
        FullContact.buildCompanyRequest()
            .domain("fullcontact.com")
            .webhookUrl("http://www.fullcontact.com/hook")
            .build();
    CompanyResponse response = fcTest.enrich(companyRequest).get();
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

    CompanyRequest companyRequest =
        FullContact.buildCompanyRequest()
            .domain("fullcontact.com")
            .webhookUrl("http://www.fullcontact.com/hook")
            .build();
    CompanyResponse response = fcTest.enrich(companyRequest).get();
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

    CompanyRequest companyRequest = FullContact.buildCompanyRequest().domain("wrong.com").build();
    CompanyResponse response = fcTest.enrich(companyRequest).get();
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

    CompanyRequest companyRequest = FullContact.buildCompanyRequest().domain("gmail.com").build();
    CompanyResponse response = fcTest.enrich(companyRequest).get();
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

    CompanyRequest companyRequest = FullContact.buildCompanyRequest().domain("fullcontact").build();
    CompanyResponse response = fcTest.enrich(companyRequest).get();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(422, response.getStatusCode());
    Assert.assertTrue(
        response
            .getMessage()
            .contains("Input domain parameter (\"fullcontact\") does not contain a valid domain."));
  }
}
