package com.fullcontact.apilib.models.Response;

import com.fullcontact.apilib.models.Location;
import com.fullcontact.apilib.models.PersonName;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class Details {
  private PersonName name;
  private Age age;
  private String gender;
  private Household household;
  private Demographics demographics;
  private Finance finance;
  private Census census;
  private Survey survey;
  private Buyer buyer;
  private List<EmailAndPhone> emails, phones;
  private Profiles profiles;
  private Identifiers identifiers;
  private Automotive automotive;
  private List<Location> locations;
  private List<Employment> employment;
  private List<Photo> photos;
  private List<Education> education;
  private List<URL> urls;
  private List<Interest> interests;
}
