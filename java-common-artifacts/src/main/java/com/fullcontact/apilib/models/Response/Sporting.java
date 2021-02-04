package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class Sporting {
  private boolean other,
      campingOrHiking,
      baseball,
      boating,
      basketball,
      fishing,
      americanFootball,
      fitness,
      golf,
      hockey,
      hunting,
      nascar,
      snowSkiing,
      walking,
      running,
      scuba,
      tennis,
      weightLifting,
      biking,
      extremeSports,
      motocross,
      skateboarding,
      snowboarding,
      rollerblading,
      interests;
}
