package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.auth.CredentialsProvider;
import com.fullcontact.apilib.auth.StaticApiKeyCredentialProvider;
import com.fullcontact.apilib.models.Request.TagsRequest;
import com.fullcontact.apilib.models.Response.TagsResponse;
import com.fullcontact.apilib.models.Tag;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class TagsResponseTest {
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
  public void tagsResponseModelDeserializationTest1()
      throws FullContactException, ExecutionException, InterruptedException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_301");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    TagsRequest tagsRequest =
        FullContact.buildTagsRequest()
            .recordId("k3")
            .tag(Tag.builder().key("gender").value("female").build())
            .build();

    TagsResponse response = fcTest.tagsCreate(tagsRequest).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertEquals("k3", response.getRecordId());
    Assert.assertEquals("gender", response.getTags().get(0).getKey());
  }

  @Test
  public void tagsResponseModelDeserializationTest2()
      throws FullContactException, ExecutionException, InterruptedException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_302");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    TagsResponse response = fcTest.tagsGet("k2").get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertEquals("k2", response.getRecordId());
    Assert.assertEquals("gender", response.getTags().get(0).getKey());
  }

  @Test
  public void tagsDeleteTest()
      throws FullContactException, ExecutionException, InterruptedException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_303");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();

    TagsRequest tagsRequest =
        FullContact.buildTagsRequest()
            .recordId("k3")
            .tag(Tag.builder().key("gender").value("female").build())
            .build();

    TagsResponse response = fcTest.tagsDelete(tagsRequest).get();
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(204, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
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

    TagsRequest tagsRequest =
        FullContact.buildTagsRequest()
            .recordId("k3")
            .tag(Tag.builder().key("gender").value("female").build())
            .build();

    TagsResponse response = fcTest.tagsCreate(tagsRequest).get();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(400, response.getStatusCode());
    Assert.assertEquals("Unable to process JSON", response.getMessage());
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

    TagsRequest tagsRequest =
        FullContact.buildTagsRequest()
            .recordId("k3")
            .tag(Tag.builder().key("gender").value("female").build())
            .build();

    TagsResponse response = fcTest.tagsCreate(tagsRequest).get();
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

    TagsRequest tagsRequest =
        FullContact.buildTagsRequest()
            .recordId("k3")
            .tag(Tag.builder().key("gender").value("female").build())
            .build();

    TagsResponse response = fcTest.tagsCreate(tagsRequest).get();
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

    TagsRequest tagsRequest =
        FullContact.buildTagsRequest()
            .recordId("k3")
            .tag(Tag.builder().key("gender").value("female").build())
            .build();

    TagsResponse response = fcTest.tagsCreate(tagsRequest).get();
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

    TagsRequest tagsRequest =
        FullContact.buildTagsRequest()
            .recordId("k3")
            .tag(Tag.builder().key("gender").value("female").build())
            .build();

    TagsResponse response = fcTest.tagsDelete(tagsRequest).get();
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(422, response.getStatusCode());
    Assert.assertTrue(
        response
            .getMessage()
            .contains("Input domain parameter (\"fullcontact\") does not contain a valid domain."));
  }
}
