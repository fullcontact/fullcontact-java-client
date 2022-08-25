package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.models.Request.AudienceRequest;
import com.fullcontact.apilib.models.Tag;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AudienceRequestBuildTest {
  private static final Gson gson = new Gson();
  @Rule public ExpectedException exceptionRule = ExpectedException.none();

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
      AudienceRequest expectedRequest = gson.fromJson(sb.toString(), AudienceRequest.class);
      Assert.assertEquals(expectedRequest, audienceRequest);
    }
  }

  @Test
  public void invalidAudienceRequestTest1() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage("WebhookUrl is mandatory for creating Audience");
    FullContact.buildAudienceRequest().build();
  }

  @Test
  public void invalidAudienceRequestTest2() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage("WebhookUrl is mandatory for creating Audience");
    FullContact.buildAudienceRequest().tag(Tag.builder().value("M").build()).build();
  }

  @Test
  public void invalidAudienceRequestTest3() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage("At least 1 Tag is mandatory for creating Audience");
    FullContact.buildAudienceRequest().webhookUrl("webhookUrl").build();
  }

  @Test
  public void invalidAudienceRequestTest4() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage("Both Key and Value must be populated for a Tag");
    FullContact.buildAudienceRequest()
        .webhookUrl("webhookUrl")
        .tag(Tag.builder().key("key").build())
        .build();
  }
}
