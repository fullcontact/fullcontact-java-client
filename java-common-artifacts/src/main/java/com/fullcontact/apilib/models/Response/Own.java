package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class Own {
  private boolean ownCat,
      ownDog,
      ownDigitalCamera,
      ownHorse,
      ownMotorcycle,
      ownSwimmingPool,
      ownAtv,
      ownRv;
}
