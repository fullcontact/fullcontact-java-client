package com.fullcontact.apilib.models;

import com.fullcontact.apilib.FullContactException;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class Profile {
  private String service, userid, username, url;

  public static class ProfileBuilder {

    public Profile build() throws FullContactException {
      this.validate();
      return new Profile(service, userid, username, url);
    }

    protected static boolean isPopulated(String value) {
      return value != null && !value.trim().isEmpty();
    }

    private void validate() throws FullContactException {
      if (isPopulated(this.url)) {
        if (isPopulated(this.username) || isPopulated(this.userid)) {
          throw new FullContactException(
              "Specifying username or userid together with url is not allowed");
        }
        return;
      } else if (isPopulated(this.service)) {
        if (isPopulated(this.userid) && isPopulated(this.username)) {
          throw new FullContactException("Specifying userid together with username is not allowed");
        } else if (isPopulated(this.userid) || isPopulated(this.username)) {
          return;
        } else {
          throw new FullContactException(
              "Either url or service plus username or userid must be set on every profiles entry.");
        }
      } else {
        throw new FullContactException(
            "Either url or service plus username or userid must be set on every profiles entry.");
      }
    }
  }
}
