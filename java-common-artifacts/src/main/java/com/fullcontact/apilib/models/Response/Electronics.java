package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
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
