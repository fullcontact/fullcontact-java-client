package com.fullcontact.apilib.models.Request;

import com.fullcontact.apilib.FullContactException;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.With;

/** Class to create requests for Company Enrich */
@Builder(toBuilder = true)
@EqualsAndHashCode
@Getter
public class CompanyRequest {
  @With private String domain, webhookUrl;

  /**
   * Method to validate request for company enrich. It validates that company domain name should not
   * be null or empty
   *
   * @throws FullContactException if domain is null or empty
   */
  public void validateForEnrich() throws FullContactException {
    if (this.domain == null || this.domain.trim().isEmpty()) {
      throw new FullContactException("Company Domain is mandatory for Company Enrich");
    }
  }
}
