package com.fullcontact.apilib.models.Response;

import lombok.*;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class CompanyResponse {
  private String name,
      location,
      twitter,
      linkedin,
      facebook,
      bio,
      logo,
      website,
      locale,
      category,
      updated;
  private int founded, employees;
  private CompanyDetails details;
  private List<DataAddOns> dataAddOns;
  public boolean isSuccessful;
  public int statusCode;
  public String message;
}
