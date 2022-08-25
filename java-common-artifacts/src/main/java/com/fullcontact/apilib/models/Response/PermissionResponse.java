package com.fullcontact.apilib.models.Response;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PermissionResponse {
  private String permissionType,
      permissionId,
      locale,
      ipAddress,
      language,
      collectionMethod,
      collectionLocation,
      policyUrl,
      termsService;
  private long timestamp, created;
  private List<ConsentPurposeResponse> consentPurposes;
}
