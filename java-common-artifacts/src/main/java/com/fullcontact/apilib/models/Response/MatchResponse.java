package com.fullcontact.apilib.models.Response;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class MatchResponse extends FCResponse {
  private String city;
  private String region;
  private String country;
  private String postalCode;
  private String familyName;
  private String givenName;
  private String phone;
  private String email;
  private Double risk;
}
