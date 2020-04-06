package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
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
