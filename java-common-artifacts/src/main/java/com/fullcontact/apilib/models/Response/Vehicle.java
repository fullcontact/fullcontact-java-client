package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Vehicle {
  private String bodyStyle, fuelType, make, model, purchaseType;
  private int purchaseDate, year;
}
