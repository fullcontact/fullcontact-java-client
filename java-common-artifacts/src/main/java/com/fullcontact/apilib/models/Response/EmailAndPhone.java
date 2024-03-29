package com.fullcontact.apilib.models.Response;

import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class EmailAndPhone {
  private String label, value, type, md5, sha256, sha1;
  private long firstSeenMs, lastSeenMs;
  private int observations;
  private double confidence;
}
