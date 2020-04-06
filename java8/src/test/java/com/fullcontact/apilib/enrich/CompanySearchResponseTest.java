package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.auth.CredentialsProvider;
import com.fullcontact.apilib.auth.StaticApiKeyCredentialProvider;
import com.fullcontact.apilib.models.Request.CompanyRequest;
import com.fullcontact.apilib.models.Response.CompanySearchResponseList;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class CompanySearchResponseTest {
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
    customHeader.put("testCode", "tc_071");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();
    CompanyRequest companyRequest =
        FullContact.buildCompanyRequest().companyName("fullcontact").build();
    CompanySearchResponseList response = fcTest.search(companyRequest).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertEquals(
        "fullcontact.com", response.getCompanySearchResponses().get(0).getLookupDomain());
    Assert.assertEquals(
        "FullContact Inc.", response.getCompanySearchResponses().get(0).getOrgName());
    Assert.assertTrue(
        response.getCompanySearchResponses().get(0).getLogo().contains("img.fullcontact.com"));
    Assert.assertEquals(
        "Denver", response.getCompanySearchResponses().get(0).getLocation().getLocality());
    Assert.assertEquals(
        "CO", response.getCompanySearchResponses().get(0).getLocation().getRegion().getName());
    Assert.assertEquals(
        "USA", response.getCompanySearchResponses().get(0).getLocation().getCountry().getName());
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
        FullContact.buildCompanyRequest().companyName("fullcontact").build();
    CompanySearchResponseList response = fcTest.search(companyRequest).get();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(400, response.getStatusCode());
    Assert.assertEquals("BadRequest", response.getMessage());
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
            .companyName("fullcontact")
            .webhookUrl("http://www.fullcontact.com/hook")
            .build();
    CompanySearchResponseList response = fcTest.search(companyRequest).get();
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
            .companyName("fullcontact")
            .webhookUrl("http://www.fullcontact.com/hook")
            .build();
    CompanySearchResponseList response = fcTest.search(companyRequest).get();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(401, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("Unauthorized"));
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

    CompanyRequest companyRequest = FullContact.buildCompanyRequest().companyName("wrong").build();
    CompanySearchResponseList response = fcTest.search(companyRequest).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(404, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("Not Found"));
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

    CompanyRequest companyRequest =
        FullContact.buildCompanyRequest().companyName("fullcontact").build();
    CompanySearchResponseList response = fcTest.search(companyRequest).get();
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

    CompanyRequest companyRequest =
        FullContact.buildCompanyRequest().companyName("fullcontact.com").build();
    CompanySearchResponseList response = fcTest.search(companyRequest).get();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(422, response.getStatusCode());
    Assert.assertTrue(
        response
            .getMessage()
            .contains("Input domain parameter (\"fullcontact\") does not contain a valid domain."));
  }
}
