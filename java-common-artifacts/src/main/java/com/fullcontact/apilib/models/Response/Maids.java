package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class Maids {
  private String id, type;
  private long firstSeenMs, lastSeenMs;
  private int observations;
  private double confidence;
}
