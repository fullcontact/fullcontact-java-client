package com.fullcontact.apilib.models.Request;

import com.fullcontact.apilib.FullContactException;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Builder(toBuilder = true)
@Getter
public class PurposeRequest {
  private Integer purposeId;
  private List<String> channel;
  private Integer ttl;
  private Boolean enabled;

  public void validate() throws FullContactException {
    if (this.getPurposeId() == null) {
      throw new FullContactException("purposeId cannot be null for consentPurposes");
    }
    if (this.getEnabled() == null) {
      throw new FullContactException("enabled cannot be null for consentPurposes");
    }
  }
}
