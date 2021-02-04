package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class Finance {
  private String cashValueBalanceHouseholdEstimate,
      financialDebtRangeEstimate,
      householdIncomeEstimate,
      netWorthRange,
      bankCard,
      retailCard;
  private boolean activeLineOfCredit, bankruptcy;
  private int discretionaryIncomeEstimate;
}
