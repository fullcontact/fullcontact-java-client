package com.fullcontact.apilib.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {
  String key;
  String value;

  public boolean isValid() {
    if (this.key != null && !this.key.isEmpty() && this.value != null && !this.value.isEmpty()) {
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    return "Tag{" + "key='" + key + "', value='" + value + "'}";
  }
}
