package com.fullcontact.apilib.models.Request;

import com.fullcontact.apilib.FullContactException;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder(toBuilder = true)
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionRequestBasic {
  @Singular private List<PurposeRequest> consentPurposes;
  private String locale;
  private String ipAddress;
  private String language;
  private String collectionMethod;
  private String collectionLocation;
  private String policyUrl;
  private String termsService;
  private String tcf;
  private Long timestamp;

  protected static boolean isPopulated(String value) {
    return value != null && !value.trim().isEmpty();
  }

  public void validate() throws FullContactException {
    if (this.getConsentPurposes() == null || this.getConsentPurposes().isEmpty()) {
      throw new FullContactException(
          "At least 1 `consentPurpose` is Required for PermissionRequest");
    } else {
      for (PurposeRequest purposeRequest : this.getConsentPurposes()) {
        purposeRequest.validate();
      }
    }
    if (!isPopulated(this.getPolicyUrl())) {
      throw new FullContactException("policyUrl is required for PermissionRequest");
    }
    if (!isPopulated(this.getTermsService())) {
      throw new FullContactException("termsService is required for PermissionRequest");
    }
    if (!isPopulated(this.getCollectionMethod())) {
      throw new FullContactException("collectionMethod is required for PermissionRequest");
    }
    if (!isPopulated(this.getCollectionLocation())) {
      throw new FullContactException("collectionLocation is required for PermissionRequest");
    }
    if (!isPopulated(this.getIpAddress())) {
      throw new FullContactException("ipAddress is required for PermissionRequest");
    }
  }
}
