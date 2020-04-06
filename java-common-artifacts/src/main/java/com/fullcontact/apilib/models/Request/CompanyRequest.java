package com.fullcontact.apilib.models.Request;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.models.enums.Sort;
import lombok.Builder;
import lombok.Getter;
import lombok.With;

/** Class to create requests for Company Enrich and Company Search */
@Builder(toBuilder = true)
@Getter
public class CompanyRequest {
  @With private String domain, companyName, webhookUrl, location, locality, region, country;
  private Sort sort;

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

  /**
   * Method to validate request for company search. It validates that companyName should not be null
   * or empty
   *
   * @throws FullContactException if companyName is null or empty
   */
  public void validateForSearch() throws FullContactException {
    if (this.companyName == null || this.companyName.trim().isEmpty()) {
      throw new FullContactException("Company Name is mandatory for Company Search.");
    }
  }
}
