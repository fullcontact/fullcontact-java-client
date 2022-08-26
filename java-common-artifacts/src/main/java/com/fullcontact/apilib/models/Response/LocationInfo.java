package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class LocationInfo {
  private String carrierRoute;
  private String coreBasedStatisticalArea;
  private String nielsenCountySize;
  private String blockGroupNumber;
  private String censusTractSuffix;
  private String countyCode;
  private String dsfSeasonCode;
  private String nielsenCountySizeCode;
}
