package com.fullcontact.apilib.models.Response;

import com.fullcontact.apilib.models.Location;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanyDetails {
  private List<Locale> locales;
  private Entity entity;
  private List<CompanyCategories> categories;
  private List<Industry> industries;
  private List<EmailAndPhone> emails, phones;
  private List<Location> locations;
  private List<Photo> images;
  private List<URL> urls;
  private List<String> keywords;
  private List<People> keyPeople;
  private CompanyTraffic traffic;
  private Profiles profiles;
}
