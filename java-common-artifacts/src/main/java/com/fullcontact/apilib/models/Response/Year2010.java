package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Year2010 {
  private String educationLevel;
  private int socioEconomicScore;
  private Average average;
  private Percent percent;
  private Median median;
  private PopulationDensity populationDensity;
}
