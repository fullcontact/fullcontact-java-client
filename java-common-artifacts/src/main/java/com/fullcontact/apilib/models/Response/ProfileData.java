package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileData {
  private String url, service, username, userid, bio;
  private int followers, following;
  private List<Photo> photos;
  private List<URL> urls;
}
