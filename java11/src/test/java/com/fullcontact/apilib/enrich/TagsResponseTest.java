package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.models.Response.TagsResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TagsResponseTest {

  @Before
  public void init() {
    System.setProperty("FC_TEST_ENV", "FC_TEST");
  }

  @After
  public void reset() {
    System.clearProperty("FC_TEST_ENV");
  }

  @Test
  public void tagsResponseModelDeserializationTest1() {
    TagsResponse response =
        FullContact.getTagsResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_301"));
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertEquals("k3", response.getRecordId());
    Assert.assertEquals("gender", response.getTags().get(0).getKey());
  }

  @Test
  public void tagsResponseModelDeserializationTest2() {
    TagsResponse response =
        FullContact.getTagsResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_302"));
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertEquals("k2", response.getRecordId());
    Assert.assertEquals("gender", response.getTags().get(0).getKey());
  }

  @Test
  public void tagsDeleteTest() {
    TagsResponse response =
        FullContact.getTagsResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_303"));
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(204, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
  }

  @Test
  public void responseStatus400Test() {
    TagsResponse response =
        FullContact.getTagsResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_002"));
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(400, response.getStatusCode());
    Assert.assertEquals("Unable to process JSON", response.getMessage());
  }

  @Test
  public void responseStatus401Test() {
    TagsResponse response =
        FullContact.getTagsResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_004"));
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(401, response.getStatusCode());
    Assert.assertTrue(
        response.getMessage().contains("401: Invalid access token: 0kj39la0c309cnw90"));
  }

  @Test
  public void responseStatus404Test() {
    TagsResponse response =
        FullContact.getTagsResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_005"));
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(404, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("Profile not found"));
  }

  @Test
  public void responseStatus403Test() {
    TagsResponse response =
        FullContact.getTagsResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_006"));
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(403, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("API Key is missing or invalid."));
  }

  @Test
  public void responseStatus422Test() {
    TagsResponse response =
        FullContact.getTagsResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_007"));
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(422, response.getStatusCode());
    Assert.assertTrue(
        response
            .getMessage()
            .contains("Input domain parameter (\"fullcontact\") does not contain a valid domain."));
  }
}
