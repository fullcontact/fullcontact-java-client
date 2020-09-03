package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Profiles {
  private ProfileData twitter,
      linkedin,
      linkedincompany,
      klout,
      youtube,
      angellist,
      owler,
      pinterest,
      github,
      lastfm,
      flickr,
      hackernews,
      foursquare,
      medium,
      keybase,
      aboutme;
}
