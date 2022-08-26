package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class FamilyInfo {
  private String householdSize;
  private String numberOfAdults;
  private String numberOfChildren;

  private LifeCycles lifeCycles;
}

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
class LifeCycles {
  private String babyBoomers;
  private String dualIncomeNoKids;
  private String familyTies;
  private String generationX;
  private String millenials;
  private String millenialsButFirstLetMeTakeASelfie;
  private String millenialsGettinHitched;
  private String millenialsIAmAnAdult;
  private String millenialsLivesWithMom;
  private String millenialsMomLife;
  private String millenialsPuttingDownRoots;
}
