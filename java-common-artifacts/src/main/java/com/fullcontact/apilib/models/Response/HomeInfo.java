package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class HomeInfo {
  private int homeValueEstimate, loanToValueEstimate, yearsInHome;
  private String dwellingType;
}
