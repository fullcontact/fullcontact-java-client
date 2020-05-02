package com.fullcontact.apilib.models.Response;

import lombok.*;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ResolveResponse {
  private List<String> recordIds, personIds;
  public boolean isSuccessful;
  public int statusCode;
  public String message;
}
