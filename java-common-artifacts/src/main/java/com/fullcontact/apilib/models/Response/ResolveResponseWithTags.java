package com.fullcontact.apilib.models.Response;

import java.util.List;
import java.util.Map;

import com.fullcontact.apilib.models.Tag;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ResolveResponseWithTags extends FCResponse {
  private List<String> recordIds, personIds, partnerIds;
  private Map<String, List<Tag>> tags;
}
