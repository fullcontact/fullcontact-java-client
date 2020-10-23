package com.fullcontact.apilib.models.Request;

import com.fullcontact.apilib.models.Tag;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

@Builder(toBuilder = true)
@Getter
public class TagsRequest {
  private String recordId;
  @Singular private List<Tag> tags;
}
