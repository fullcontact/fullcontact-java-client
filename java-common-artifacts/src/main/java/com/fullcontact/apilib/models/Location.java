package com.fullcontact.apilib.models;

import lombok.*;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@Getter
public class Location {
  private String addressLine1,
      addressLine2,
      city,
      region,
      regionCode,
      postalCode,
      country,
      countryCode,
      formatted,
      type,
      label;
}
