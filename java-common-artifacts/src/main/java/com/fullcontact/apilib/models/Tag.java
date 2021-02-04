package com.fullcontact.apilib.models;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class Tag {
  private String key;
  private String value;

  public boolean isValid() {
    return this.key != null
        && !this.key.isEmpty()
        && this.value != null
        && !this.value.isEmpty()
        && !this.key.contains("'");
  }

  @Override
  public String toString() {
    return "Tag{" + "key='" + key + "', value='" + value + "'}";
  }
}
