package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class Reading {
  private String bibleOrDevotional;
  private String bestSellingFiction;
  private String childrens;
  private String fashion;
  private String military;
  private String entertainment;
  private String romance;
  private String sports;
  private String books;
  private String cookingOrCulinary;
  private String countryOrLifestyle;
  private String interior;
  private String medicalOrHealth;
  private String worldNews;
}
