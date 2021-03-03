package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Getter
public class ConsentPurposeResponse {
  private int ttl, purposeId;
  private boolean enabled;
  private String channel, purposeName;
  private long timestamp;
}
