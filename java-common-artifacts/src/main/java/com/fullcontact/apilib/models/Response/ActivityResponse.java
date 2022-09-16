package com.fullcontact.apilib.models.Response;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class ActivityResponse extends FCResponse {
  Double emails;
  Double online;
  Double social;
  Double employment;
}
