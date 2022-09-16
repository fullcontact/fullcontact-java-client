package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class Demographics {
  private String gender;
  private Age age;
  private Consumers consumers;
  private HomeInfo homeInfo;
  private Enthusiasts enthusiasts;
  private MaritalInfo maritalInfo;

  @Getter
  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  @NoArgsConstructor
  public static class HomeInfo {
    private String homeOwner;
    private String homeOwnerIndicator;
  }
}

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
class Consumers {
  private String valueScore;
}

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
class Enthusiasts {
  private String niches;
  private String politicalParty;
}

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
class MaritalInfo {
  private String maritalStatus;
  private String maritalStatusIndicator;
}
