package com.fullcontact.apilib.models.Response;

import lombok.*;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class CompanyResponse extends FCResponse {
  private String name, location, twitter, linkedin, bio, logo, website, locale, category, updated;
  private int founded, employees;
  private CompanyDetails details;
}
