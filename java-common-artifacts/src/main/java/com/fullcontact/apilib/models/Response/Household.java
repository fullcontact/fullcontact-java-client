package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class Household {
  private HomeInfo homeInfo;
  private Presence presence;
  private Finance finance;
  private LocationInfo locationInfo;
  private FamilyInfo familyInfo;
  private ChildrenInfo childrenInfo;
}
