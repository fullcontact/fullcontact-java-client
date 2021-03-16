package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class PermissionCurrentResponseMap extends FCResponse {
  public Map<Integer, Map<String, ConsentPurposeResponse>> responseMap;
}
