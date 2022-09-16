package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class Music {
  private String christianOrGospel;
  private String classical;
  private String country;
  private String jazz;
  private String any;
  private String rythmAndBlues;
  private String rockNRoll;
}
