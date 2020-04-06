package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class CompanySearchResponseList {
  public List<CompanySearchResponse> companySearchResponses;
  public String message;
  public int statusCode;
  public boolean isSuccessful;
}
