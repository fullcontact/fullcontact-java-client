package com.fullcontact.apilib.models.Response;

import com.fullcontact.apilib.models.Tag;
import lombok.*;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ResolveResponse {
  private List<String> recordIds, personIds, partnerIds;
  private List<Tag> tags;
  public boolean isSuccessful;
  public int statusCode;
  public String message;
}
