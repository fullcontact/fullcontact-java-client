package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class Music {
  private boolean general,
      christianOrGospel,
      classical,
      country,
      jazz,
      other,
      rhythmAndBlues,
      rock,
      softRock,
      swing,
      alternative;
}
