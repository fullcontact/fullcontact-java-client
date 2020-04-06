package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Median {
  private int ageOfHouseholder,
      effectiveBuyingIncome,
      homeValue,
      householdIncome,
      householdIncomeByState;
}
