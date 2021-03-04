package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.models.Response.FCResponse;
import com.fullcontact.apilib.models.Response.PersonResponse;
import org.junit.Assert;
import org.junit.Test;

// Tests for Permission Create and Delete APIs Response
public class FCResponseTest {
  @Test
  public void fCResponseModelDeserializationTest() {
    FCResponse response =
        FullContact.getFCResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_501"), FCResponse.class);
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(202, response.getStatusCode());
  }

  @Test
  public void responseStatus400Test() {
    PersonResponse personResponse =
        (PersonResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_002"),
                PersonResponse.class);
    Assert.assertFalse(personResponse.isSuccessful());
    Assert.assertEquals(400, personResponse.getStatusCode());
    Assert.assertEquals("Unable to process JSON", personResponse.getMessage());
  }

  @Test
  public void responseStatus401Test() {
    PersonResponse response =
        (PersonResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_004"),
                PersonResponse.class);
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(401, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("401: Invalid access token"));
  }

  @Test
  public void responseStatus404Test() {
    PersonResponse response =
        (PersonResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_005"),
                PersonResponse.class);
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(404, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("Profile not found"));
  }

  @Test
  public void responseStatus403Test() {
    PersonResponse response =
        (PersonResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_006"),
                PersonResponse.class);
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(403, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("API Key is missing or invalid."));
  }

  @Test
  public void responseStatus422Test() {
    PersonResponse response =
        (PersonResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_007"),
                PersonResponse.class);
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(422, response.getStatusCode());
    Assert.assertTrue(
        response
            .getMessage()
            .contains("Input domain parameter (\"fullcontact\") does not contain a valid domain."));
  }
}
