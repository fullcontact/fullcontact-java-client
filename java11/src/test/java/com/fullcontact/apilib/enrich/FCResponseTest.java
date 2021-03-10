package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.models.Response.FCResponse;
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
    FCResponse response =
        FullContact.getFCResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_002"), FCResponse.class);
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(400, response.getStatusCode());
    Assert.assertEquals("Unable to process JSON", response.getMessage());
  }

  @Test
  public void responseStatus401Test() {
    FCResponse response =
        FullContact.getFCResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_004"), FCResponse.class);
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(401, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("401: Invalid access token"));
  }

  @Test
  public void responseStatus404Test() {
    FCResponse response =
        FullContact.getFCResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_005"), FCResponse.class);
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(404, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("Profile not found"));
  }

  @Test
  public void responseStatus403Test() {
    FCResponse response =
        FullContact.getFCResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_006"), FCResponse.class);
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(403, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("API Key is missing or invalid."));
  }

  @Test
  public void responseStatus422Test() {
    FCResponse response =
        FullContact.getFCResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_007"), FCResponse.class);
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(422, response.getStatusCode());
    Assert.assertTrue(
        response
            .getMessage()
            .contains("Input domain parameter (\"fullcontact\") does not contain a valid domain."));
  }
}
