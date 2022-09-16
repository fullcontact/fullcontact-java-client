package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class Travel {
  private String timeshare;
  private String business;
  private String cruiseShipVacation;
  private String international;
  private String leisure;
  private String rvVacations;
  private String travelInTheUSA;
  private String traveler;
}
