package com.fullcontact.apilib.models.Response;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class ProfileData {
  private String url, service, username, userid, bio;
  private int followers, following;
  private List<Photo> photos;
  private List<URL> urls;
}
