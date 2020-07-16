package com.fullcontact.apilib.models.Response;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class EmailProperties {
  private String message, address, username, domain, person, company;
  private boolean corrected, sendSafely;
  private EmailAttributes attributes;
}
