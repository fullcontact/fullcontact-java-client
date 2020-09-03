package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class LocationInfo {
  private String carrierRoute,
      designatedMarketArea,
      coreBasedStatisticalArea,
      nielsenCountySize,
      congressionalDistrict;
  private int numericCountyCode;
  private boolean seasonalAddress;
}
