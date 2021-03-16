package com.fullcontact.apilib.models.Request;

import com.fullcontact.apilib.FullContactException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder(builderMethodName = "permissionRequestBuilder", toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public class PermissionRequest extends PermissionRequestBasic {
  private MultifieldRequest query;

  public void validate() throws FullContactException {
    super.validate();
    if (this.getQuery() == null) {
      throw new FullContactException("Query is required for PermissionRequest");
    }
    this.getQuery().validate();
  }
}
