package com.fullcontact.apilib.models.Response;

import com.fullcontact.apilib.models.Tag;
import lombok.*;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class TagsResponse extends FCResponse {
  private String recordId, partnerId;
  private List<Tag> tags;
}
