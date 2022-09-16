package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class Finance {
  private String income;
  private String incomeIndicator;
  private String narrowBandIncome;
  private String discretionarySpendingIncome;
  private String firstMortgageAmountInThousands;
  private String homeEquityLoanDate;
  private String homeMarketValueTaxRecord;
  private String homeEquityLoanInThousands;
  private String homeEquityLoanIndicator;
  private String investmentResources;
  private String liquidResources;
  private String mortgageInterestRateTypeOrRefinance;
  private String mortgageLiability;
  private String mortgageLoanTypeOrRefinance;
  private String mortgageDate;
  private String refinanceIndicator;
  private String secondMortgageAmountInThousands;
  private String shortTermLiability;
  private String incomeIndex;
  private String netWorth;
  private String wealthResources;
  private String paymentMethodCreditCard;
}
