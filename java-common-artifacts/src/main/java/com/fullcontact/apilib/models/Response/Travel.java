package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Travel {
  private boolean general,
      usBusiness,
      internationalBusiness,
      usPersonal,
      internationalPersonal,
      casinoVacations,
      familyVacations,
      frequentFlyer,
      timeshare,
      vacationCruises,
      attractionsOrThemeParks,
      rv;
}
