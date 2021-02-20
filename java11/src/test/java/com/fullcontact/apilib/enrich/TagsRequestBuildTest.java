package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.models.Request.TagsRequest;
import com.fullcontact.apilib.models.Tag;
import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TagsRequestBuildTest {
  private static final Gson gson = new Gson();

  @Test
  public void tagsRequestBuildAndSerializeTest() throws FullContactException, IOException {
    List<Tag> tags = new ArrayList<>();
    tags.add(Tag.builder().key("key").value("value").build());
    tags.add(Tag.builder().key("gender").value("male").build());
    TagsRequest tagsRequest =
        FullContact.buildTagsRequest()
            .recordId("customer123")
            .tags(tags)
            .tag(Tag.builder().key("gender").value("M").build())
            .build();
    try (BufferedReader br =
        new BufferedReader(new FileReader("src/test/resources/tagsRequestBuildTest.txt"))) {
      String line;
      StringBuilder sb = new StringBuilder();
      while ((line = br.readLine()) != null) {
        sb.append(line.trim());
      }
      TagsRequest expectedRequest = gson.fromJson(sb.toString(), TagsRequest.class);
      Assert.assertEquals(expectedRequest, tagsRequest);
    }
  }

  @Test
  public void invalidTagsRequestTest1() {
    try {
      TagsRequest tagsRequest =
          FullContact.buildTagsRequest()
              .recordId("customer123")
              .tag(Tag.builder().key("gender").build())
              .build();
    } catch (FullContactException e) {
      Assert.assertEquals("Both Key and Value must be populated for adding a Tag", e.getMessage());
    }
  }

  @Test
  public void invalidTagsRequestTest2() {
    try {
      TagsRequest tagsRequest =
          FullContact.buildTagsRequest()
              .recordId("customer123")
              .tag(Tag.builder().value("M").build())
              .build();
    } catch (FullContactException e) {
      Assert.assertEquals("Both Key and Value must be populated for adding a Tag", e.getMessage());
    }
  }

  @Test
  public void invalidTagsRequestTest3() {
    try {
      TagsRequest tagsRequest =
          FullContact.buildTagsRequest().tag(Tag.builder().key("G").value("M").build()).build();
    } catch (FullContactException e) {
      Assert.assertEquals("RecordId must be present for creating Tags", e.getMessage());
    }
  }
}
