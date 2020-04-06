package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Percent {
  private int abovePovertyLevel,
      belowPovertyLevel,
      black,
      blueCollarEmployed,
      divorcedOrSeparated,
      hispanic,
      homesBuiltSince2000,
      homeowner,
      householdsWithChildren,
      married,
      mobileHome,
      movedToAreaSince2000,
      salariedProfessional,
      singleFamilyHome,
      vehicleOwnership,
      white;
}
