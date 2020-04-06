package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.models.Response.CompanyResponse;
import org.junit.Assert;
import org.junit.Test;

public class CompanyEnrichResponse {

  @Test
  public void companyResponseModelDeserializationTest() {
    CompanyResponse response =
        FullContact.getCompanyResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_051"));
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
  public void responseStatus400Test() {
    CompanyResponse personResponse =
        FullContact.getCompanyResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_002"));
    Assert.assertFalse(personResponse.isSuccessful());
    Assert.assertEquals(400, personResponse.getStatusCode());
    Assert.assertEquals("Unable to process JSON", personResponse.getMessage());
  }

  @Test
  public void responseStatus202Test() {
    CompanyResponse response =
        FullContact.getCompanyResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_003"));
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(202, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("Queued for search"));
  }

  @Test
  public void responseStatus401Test() {
    CompanyResponse response =
        FullContact.getCompanyResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_004"));
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(401, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("401: Invalid access token"));
  }

  @Test
  public void responseStatus404Test() {
    CompanyResponse response =
        FullContact.getCompanyResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_005"));
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(404, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("Profile not found"));
  }

  @Test
  public void responseStatus403Test() {
    CompanyResponse response =
        FullContact.getCompanyResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_006"));
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(403, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("API Key is missing or invalid."));
  }

  @Test
  public void responseStatus422Test() {
    CompanyResponse response =
        FullContact.getCompanyResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_007"));
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(422, response.getStatusCode());
    Assert.assertTrue(
        response
            .getMessage()
            .contains("Input domain parameter (\"fullcontact\") does not contain a valid domain."));
  }
}
