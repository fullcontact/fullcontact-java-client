package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.models.Response.ActivityResponse;
import com.fullcontact.apilib.models.Response.MatchResponse;
import com.fullcontact.apilib.models.Response.SignalsResponse;
import org.junit.Assert;
import org.junit.Test;

public class VerifyResponseTest {
  @Test
  public void signalsResponseModelDeserializationTest() {
    SignalsResponse response =
        (SignalsResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_601"),
                SignalsResponse.class);
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
    Assert.assertEquals("Chip", response.getName().getGivenName());
    Assert.assertEquals("Shipperman", response.getName().getFamilyName());
    Assert.assertEquals(
        "https://twitter.com/ChipShipperman", response.getSocialProfiles().getTwitterUrl());
  }

  @Test
  public void activityResponseModelDeserializationTest() {
    ActivityResponse response =
        (ActivityResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_602"),
                ActivityResponse.class);
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertEquals("0.82", String.valueOf(response.getEmails()));
    Assert.assertEquals("0.32", String.valueOf(response.getSocial()));
    Assert.assertEquals("0.78", String.valueOf(response.getEmployment()));
    Assert.assertEquals("0.91", String.valueOf(response.getOnline()));
  }

  @Test
  public void matchResponseModelDeserializationTest() {
    MatchResponse response =
        (MatchResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_603"),
                MatchResponse.class);
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertEquals("self", response.getEmail());
    Assert.assertEquals("household", response.getFamilyName());
    Assert.assertEquals("household", response.getPostalCode());
    Assert.assertEquals("household", response.getCountry());
    Assert.assertEquals("household", response.getCity());
  }

  @Test
  public void responseStatus400Test() {
    SignalsResponse personResponse =
        (SignalsResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_002"),
                SignalsResponse.class);
    Assert.assertFalse(personResponse.isSuccessful());
    Assert.assertEquals(400, personResponse.getStatusCode());
    Assert.assertEquals("Unable to process JSON", personResponse.getMessage());
  }

  @Test
  public void responseStatus401Test() {
    ActivityResponse response =
        (ActivityResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_004"),
                ActivityResponse.class);
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(401, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("401: Invalid access token"));
  }

  @Test
  public void responseStatus404Test() {
    MatchResponse response =
        (MatchResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_005"),
                MatchResponse.class);
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(404, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("Profile not found"));
  }

  @Test
  public void responseStatus403Test() {
    SignalsResponse response =
        (SignalsResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_006"),
                SignalsResponse.class);
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(403, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("API Key is missing or invalid."));
  }

  @Test
  public void responseStatus422Test() {
    SignalsResponse response =
        (SignalsResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_007"),
                SignalsResponse.class);
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(422, response.getStatusCode());
    Assert.assertTrue(
        response
            .getMessage()
            .contains("Input domain parameter (\"fullcontact\") does not contain a valid domain."));
  }
}
