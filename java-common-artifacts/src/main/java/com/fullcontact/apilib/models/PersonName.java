package com.fullcontact.apilib.models;

import lombok.*;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Builder
@Getter
public class PersonName {
  private String full, given, family;
}
