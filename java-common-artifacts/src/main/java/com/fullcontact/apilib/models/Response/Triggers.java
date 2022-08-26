package com.fullcontact.apilib.models.Response;

import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class Triggers {
  private Type type;
  private Date date;

  @Getter
  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  @NoArgsConstructor
  public static class Date {
    private String collegeGraduateChange;
    private String emptyNesterChange;
    private String firstChildChange;
    private String homeMarketValueChange;
    private String incomeChange;
    private String newAdultChange;
    private String newDriverChange;
    private String newYoungAdultToChange;
    private String nicheSwitch;
    private String valueScoreChange;
  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  @NoArgsConstructor
  public static class Type {
    private String collegeGraduate;
    private String emptyNester;
    private String homeMarketValue;
    private String income;
    private String newAdultToFile;
    private String newFirstChild0to2YearsOld;
    private String newPreDriver;
    private String newYoungAdultToFile;
    private String nicheSwitch;
    private String valueScore;
  }
}
