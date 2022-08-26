package com.fullcontact.apilib.models.Response;

import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class ChildrenInfo {
  private Gender gender;
  private BirthDate birthDate;
}

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
class Gender {
  private String firstChild;
  private String secondChild;
  private String thirdChild;
  private String fourthChild;
}

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
class BirthDate {
  private String firstChild;
  private String secondChild;
  private String thirdChild;
  private String fourthChild;
}
