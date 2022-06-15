package com.fullcontact.apilib.models.Response;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class MatchResponse extends FCResponse {
  private Boolean city,
      region,
      country,
      continent,
      postalCode,
      familyName,
      givenName,
      phone,
      email,
      maid,
      social,
      nonId;
}
