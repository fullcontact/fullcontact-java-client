package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class HomeInfo {
  private String dwellingType;
  private String dwellingTypeIndicator;
  private String householdEducation;
  private String householdEducationIndicator;
  private String maritalStatus;
  private String lengthOfResidence;
  private String lengthOfResidenceIndicator;
  private String affluents;
  private String currentLoanToValue;
  private String homeHeatSource;
  private String householdOccupation;
  private String familyComposition;
  private String homeMarketValue;
  private String ownerToOwner;
  private String rentertoOwner;
  private String yearHomeBuilt;
}
