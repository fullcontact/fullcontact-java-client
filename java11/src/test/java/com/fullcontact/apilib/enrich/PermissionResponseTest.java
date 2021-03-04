package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.models.Response.ConsentPurposeResponse;
import com.fullcontact.apilib.models.Response.PermissionCurrentResponseMap;
import com.fullcontact.apilib.models.Response.PermissionResponseList;
import org.junit.Assert;
import org.junit.Test;

public class PermissionResponseTest {

  // Permission Find Response Test - Start
  @Test
  public void permissionResponseListModelDeserializationTest() {
    PermissionResponseList response =
        FullContact.getPermissionFindResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_502"));
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("create", response.permissionResponseList.get(0).getPermissionType());
    Assert.assertEquals(
        "web", response.permissionResponseList.get(0).getConsentPurposes().get(0).getChannel());
    Assert.assertEquals(
        6, response.permissionResponseList.get(0).getConsentPurposes().get(0).getPurposeId());
    Assert.assertEquals("cookie", response.permissionResponseList.get(0).getCollectionMethod());
    Assert.assertEquals(
        "web", response.permissionResponseList.get(1).getConsentPurposes().get(0).getChannel());
    Assert.assertTrue(
        response.permissionResponseList.get(0).getConsentPurposes().get(0).isEnabled());
  }

  @Test
  public void responseStatus400Test() {
    PermissionResponseList response =
        FullContact.getPermissionFindResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_002"));
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(400, response.getStatusCode());
    Assert.assertEquals("Unable to process JSON", response.getMessage());
  }

  @Test
  public void responseStatus401Test() {
    PermissionResponseList response =
        FullContact.getPermissionFindResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_004"));
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(401, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("401: Invalid access token"));
  }

  @Test
  public void responseStatus404Test() {
    PermissionResponseList response =
        FullContact.getPermissionFindResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_005"));
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(404, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("Profile not found"));
  }

  @Test
  public void responseStatus403Test() {
    PermissionResponseList response =
        FullContact.getPermissionFindResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_006"));
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(403, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("API Key is missing or invalid."));
  }

  @Test
  public void responseStatus422Test() {
    PermissionResponseList response =
        FullContact.getPermissionFindResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_007"));
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(422, response.getStatusCode());
    Assert.assertTrue(
        response
            .getMessage()
            .contains("Input domain parameter (\"fullcontact\") does not contain a valid domain."));
  }

  // Permission Find Response Test - End

  // Permission Current Response Test - Start
  @Test
  public void permissionCurrentResponseMapModelDeserializationTest() {
    PermissionCurrentResponseMap response =
        FullContact.getPermissionCurrentResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_503"));
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertTrue(response.getResponseMap().get(3).get("mobile").isEnabled());
    Assert.assertEquals("email", response.getResponseMap().get(3).get("email").getChannel());
    Assert.assertEquals(3, response.getResponseMap().get(3).get("phone").getPurposeId());
    Assert.assertTrue(response.getResponseMap().get(6).get("web").isEnabled());
    Assert.assertEquals("offline", response.getResponseMap().get(10).get("offline").getChannel());
    Assert.assertEquals("phone", response.getResponseMap().get(10).get("phone").getChannel());
  }

  @Test
  public void permissionCurrentResponseStatus400Test() {
    PermissionCurrentResponseMap response =
        FullContact.getPermissionCurrentResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_002"));
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(400, response.getStatusCode());
    Assert.assertEquals("Unable to process JSON", response.getMessage());
  }

  @Test
  public void permissionCurrentResponseStatus401Test() {
    PermissionCurrentResponseMap response =
        FullContact.getPermissionCurrentResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_004"));
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(401, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("401: Invalid access token"));
  }

  @Test
  public void permissionCurrentResponseStatus404Test() {
    PermissionCurrentResponseMap response =
        FullContact.getPermissionCurrentResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_005"));
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(404, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("Profile not found"));
  }

  @Test
  public void permissionCurrentResponseStatus403Test() {
    PermissionCurrentResponseMap response =
        FullContact.getPermissionCurrentResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_006"));
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(403, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("API Key is missing or invalid."));
  }

  @Test
  public void permissionCurrentResponseStatus422Test() {
    PermissionCurrentResponseMap response =
        FullContact.getPermissionCurrentResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_007"));
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(422, response.getStatusCode());
    Assert.assertTrue(
        response
            .getMessage()
            .contains("Input domain parameter (\"fullcontact\") does not contain a valid domain."));
  }

  // Permission Current Response Test - End

  // Permission Verify Response Test - Start

  @Test
  public void consentPurposeResponseModelDeserializationTest() {
    ConsentPurposeResponse response =
        (ConsentPurposeResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_504"),
                ConsentPurposeResponse.class);
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertEquals(6, response.getPurposeId());
    Assert.assertTrue(response.isEnabled());
    Assert.assertEquals("Content selection, delivery & reporting", response.getPurposeName());
  }

  @Test
  public void consentPurposeResponseStatus400Test() {
    ConsentPurposeResponse response =
        (ConsentPurposeResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_002"),
                ConsentPurposeResponse.class);
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(400, response.getStatusCode());
    Assert.assertEquals("Unable to process JSON", response.getMessage());
  }

  @Test
  public void consentPurposeResponseStatus202Test() {
    ConsentPurposeResponse response =
        (ConsentPurposeResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_003"),
                ConsentPurposeResponse.class);
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(202, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("Queued for search"));
  }

  @Test
  public void consentPurposeResponseStatus401Test() {
    ConsentPurposeResponse response =
        (ConsentPurposeResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_004"),
                ConsentPurposeResponse.class);
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(401, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("401: Invalid access token"));
  }

  @Test
  public void consentPurposeResponseStatus404Test() {
    ConsentPurposeResponse response =
        (ConsentPurposeResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_005"),
                ConsentPurposeResponse.class);
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(404, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("Profile not found"));
  }

  @Test
  public void consentPurposeResponseStatus403Test() {
    ConsentPurposeResponse response =
        (ConsentPurposeResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_006"),
                ConsentPurposeResponse.class);
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(403, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("API Key is missing or invalid."));
  }

  @Test
  public void consentPurposeResponseStatus422Test() {
    ConsentPurposeResponse response =
        (ConsentPurposeResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_007"),
                ConsentPurposeResponse.class);
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(422, response.getStatusCode());
    Assert.assertTrue(
        response
            .getMessage()
            .contains("Input domain parameter (\"fullcontact\") does not contain a valid domain."));
  }

  // Permission Verify Response Test - End
}
