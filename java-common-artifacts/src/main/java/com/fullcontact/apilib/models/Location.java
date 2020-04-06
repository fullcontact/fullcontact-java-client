package com.fullcontact.apilib.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
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
