package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class Presence {
  private String presenceOfChildren;
  private String presenceOfChildrenIndicator;

  private Adult adult;
  private Child child;
}

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
class Adult {
  private String age18to24;
  private String age25to34;
  private String age35to44;
  private String age45to54;
  private String age55to64;
  private String age65to74;
  private String age75above;
}

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
class Child {
  private String age0to2;
  private String age11to15;
  private String age16to17;
  private String age3to5;
  private String age6to10;
}
