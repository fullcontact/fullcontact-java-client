package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.models.Request.AudienceRequest;
import com.fullcontact.apilib.models.Tag;
import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AudienceRequestBuildTest {
  private static final Gson gson = new Gson();

  @Test
  public void AudienceRequestBuildAndSerializeTest() throws FullContactException, IOException {
    List<Tag> tags = new ArrayList<>();
    tags.add(Tag.builder().key("key").value("value").build());
    tags.add(Tag.builder().key("gender").value("male").build());
    AudienceRequest audienceRequest =
        FullContact.buildAudienceRequest()
            .tags(tags)
            .tag(Tag.builder().key("gender").value("M").build())
            .webhookUrl("https://webhookUrl.com/test")
            .build();
    try (BufferedReader br =
        new BufferedReader(new FileReader("src/test/resources/audienceRequestBuildTest.txt"))) {
      String line;
      StringBuilder sb = new StringBuilder();
      while ((line = br.readLine()) != null) {
        sb.append(line.trim());
      }
      Assert.assertEquals(sb.toString(), gson.toJson(audienceRequest));
    }
  }

  @Test
  public void invalidAudienceRequestTest1() {
    try {
      AudienceRequest audienceRequest = FullContact.buildAudienceRequest().build();
    } catch (FullContactException e) {
      Assert.assertEquals("WebhookUrl is mandatory for creating Audience", e.getMessage());
    }
  }

  @Test
  public void invalidAudienceRequestTest2() {
    try {
      AudienceRequest audienceRequest =
          FullContact.buildAudienceRequest().tag(Tag.builder().value("M").build()).build();
    } catch (FullContactException e) {
      Assert.assertEquals("WebhookUrl is mandatory for creating Audience", e.getMessage());
    }
  }

  @Test
  public void invalidAudienceRequestTest3() {
    try {
      AudienceRequest audienceRequest =
          FullContact.buildAudienceRequest().webhookUrl("webhookUrl").build();
    } catch (FullContactException e) {
      Assert.assertEquals("Atleast 1 Tag is mandatory for creating Audience", e.getMessage());
    }
  }

  @Test
  public void invalidAudienceRequestTest4() {
    try {
      AudienceRequest audienceRequest =
          FullContact.buildAudienceRequest()
              .webhookUrl("webhookUrl")
              .tag(Tag.builder().key("key").build())
              .build();
    } catch (FullContactException e) {
      Assert.assertEquals("Both Key and Value must be populated for a Tag", e.getMessage());
    }
  }
}
