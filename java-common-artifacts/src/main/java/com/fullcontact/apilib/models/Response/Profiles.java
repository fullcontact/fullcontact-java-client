package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
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
