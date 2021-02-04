package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class Median {
  private int ageOfHouseholder,
      effectiveBuyingIncome,
      homeValue,
      householdIncome,
      householdIncomeByState;
}
