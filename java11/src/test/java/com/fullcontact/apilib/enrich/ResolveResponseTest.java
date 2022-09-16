package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.models.Response.ResolveResponse;
import com.fullcontact.apilib.models.Response.ResolveResponseWithTags;
import java.util.HashMap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
  public void resolveResponseModelDeserializationTest1() {
    ResolveResponse response =
        (ResolveResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_101"),
                ResolveResponse.class);
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertEquals("21c300bcf16b079ae52025cc1c06765c", response.getRecordIds().get(0));
    Assert.assertEquals("key", response.getTags().get(0).getKey());
  }

  @Test
  public void resolveResponseModelDeserializationTest2() {
    ResolveResponse response =
        (ResolveResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_102"),
                ResolveResponse.class);
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertEquals("customer123", response.getRecordIds().get(0));
    Assert.assertEquals(
        "VS1OPPPPvxHcCNPezUbvYBCDEAOdSj5AI0adsA2bLmh12345", response.getPersonIds().get(0));
  }

  @Test
  public void identityDeleteTest() {
    ResolveResponse response =
        (ResolveResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_103"),
                ResolveResponse.class);
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(204, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertNull(response.getRecordIds());
    Assert.assertNull(response.getPersonIds());
  }

  @Test
  public void responseStatus400Test() {
    ResolveResponse personResponse =
        (ResolveResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_002"),
                ResolveResponse.class);
    Assert.assertFalse(personResponse.isSuccessful());
    Assert.assertEquals(400, personResponse.getStatusCode());
    Assert.assertEquals("Unable to process JSON", personResponse.getMessage());
  }

  @Test
  public void responseStatus401Test() {
    ResolveResponse response =
        (ResolveResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_004"),
                ResolveResponse.class);
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(401, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("401: Invalid access token"));
  }

  @Test
  public void responseStatus404Test() {
    ResolveResponse response =
        (ResolveResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_005"),
                ResolveResponse.class);
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(404, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("Profile not found"));
  }

  @Test
  public void responseStatus403Test() {
    ResolveResponse response =
        (ResolveResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_006"),
                ResolveResponse.class);
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(403, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("API Key is missing or invalid."));
  }

  @Test
  public void responseStatus422Test() {
    ResolveResponse response =
        (ResolveResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_007"),
                ResolveResponse.class);
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(422, response.getStatusCode());
    Assert.assertTrue(
        response
            .getMessage()
            .contains("Input domain parameter (\"fullcontact\") does not contain a valid domain."));
  }

  @Test
  public void resolveResponseWithTagsModelDeserializationTest() {
    ResolveResponseWithTags response =
        (ResolveResponseWithTags)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_104"),
                ResolveResponseWithTags.class);
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertEquals("customer123", response.getRecordIds().get(0));
    Assert.assertEquals(
        "VS1OPPPPvxHcCNPezUbvYBCDEAOdSj5AI0adsA2bLmh12345", response.getPersonIds().get(0));
    Assert.assertEquals("gender", response.getTags().get("customer123").get(0).getKey());
  }
}
