package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Electronics {
  private boolean appleDevice,
      cableTv,
      highSpeedInternet,
      dvr,
      dvdPlayer,
      hdtv,
      homeTheater,
      satelliteRadio,
      satelliteTv,
      videoGameSystems,
      other;
}
