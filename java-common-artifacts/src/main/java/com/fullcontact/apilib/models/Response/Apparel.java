package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
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
