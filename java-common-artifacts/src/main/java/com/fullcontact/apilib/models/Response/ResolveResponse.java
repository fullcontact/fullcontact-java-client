package com.fullcontact.apilib.models.Response;

import com.fullcontact.apilib.models.Tag;
import java.util.List;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ResolveResponse extends FCResponse {
  private List<String> recordIds, personIds, partnerIds;
  private List<Tag> tags;
}
