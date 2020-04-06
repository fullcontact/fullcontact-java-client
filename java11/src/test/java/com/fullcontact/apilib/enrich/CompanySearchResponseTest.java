package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.models.Response.CompanySearchResponseList;
import org.junit.Assert;
import org.junit.Test;

public class CompanySearchResponseTest {

  @Test
  public void companyResponseModelDeserializationTest() {
    CompanySearchResponseList response =
        FullContact.getCompanySearchResponseList(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_071"));
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
  public void responseStatus400Test() {
    CompanySearchResponseList personResponse =
        FullContact.getCompanySearchResponseList(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_002"));
    Assert.assertFalse(personResponse.isSuccessful());
    Assert.assertEquals(400, personResponse.getStatusCode());
    Assert.assertEquals("Unable to process JSON", personResponse.getMessage());
  }

  @Test
  public void responseStatus202Test() {
    CompanySearchResponseList response =
        FullContact.getCompanySearchResponseList(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_003"));
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(202, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("Queued for search"));
  }

  @Test
  public void responseStatus401Test() {
    CompanySearchResponseList response =
        FullContact.getCompanySearchResponseList(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_004"));
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(401, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("401: Invalid access token"));
  }

  @Test
  public void responseStatus404Test() {
    CompanySearchResponseList response =
        FullContact.getCompanySearchResponseList(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_005"));
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(404, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("Profile not found"));
  }

  @Test
  public void responseStatus403Test() {
    CompanySearchResponseList response =
        FullContact.getCompanySearchResponseList(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_006"));
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(403, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("API Key is missing or invalid."));
  }

  @Test
  public void responseStatus422Test() {
    CompanySearchResponseList response =
        FullContact.getCompanySearchResponseList(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_007"));
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(422, response.getStatusCode());
    Assert.assertTrue(
        response
            .getMessage()
            .contains("Input domain parameter (\"fullcontact\") does not contain a valid domain."));
  }
}
