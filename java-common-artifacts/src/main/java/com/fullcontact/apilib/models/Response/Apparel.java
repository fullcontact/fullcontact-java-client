package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Apparel {
  private boolean general,
      children,
      men,
      mensBigTall,
      nonGenderSpecific,
      teenagers,
      women,
      womenPetiteSize,
      womenPlusSize;
}
