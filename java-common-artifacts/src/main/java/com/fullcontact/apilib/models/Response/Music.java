package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
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
