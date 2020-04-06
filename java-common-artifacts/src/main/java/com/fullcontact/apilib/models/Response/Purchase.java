package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Purchase {
  private boolean homeDecorating,
      beautyProducts,
      clubStores,
      fastFoods,
      specialtyBeautyProducts,
      usesCoupons;
}
