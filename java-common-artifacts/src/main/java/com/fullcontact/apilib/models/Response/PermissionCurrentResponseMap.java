package com.fullcontact.apilib.models.Response;

import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class PermissionCurrentResponseMap extends FCResponse {
  public Map<Integer, Map<String, ConsentPurposeResponse>> responseMap;
}
