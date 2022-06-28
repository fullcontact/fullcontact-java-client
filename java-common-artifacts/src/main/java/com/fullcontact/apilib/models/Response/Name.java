package com.fullcontact.apilib.models.Response;

import lombok.*;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Builder
@Getter
public class Name {
  private String givenName, familyName;
}
