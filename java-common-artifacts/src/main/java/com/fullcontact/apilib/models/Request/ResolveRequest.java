package com.fullcontact.apilib.models.Request;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.models.Tag;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

/** Class to create request for Resolve */
@Getter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(builderMethodName = "resolveRequestBuilder", toBuilder = true)
public class ResolveRequest extends MultifieldRequest {
  @Singular private List<Tag> tags;
  private boolean generatePid;

  /**
   * Method to validate request for Identity map. It validates that personId should be null and must
   * contain any one of person identifier
   *
   * @throws FullContactException if validation fails
   */
  public void validateForIdentityMap() throws FullContactException {
    this.validate();
    if (this.getPersonId() != null) {
      throw new FullContactException("Invalid map request, person id must be empty");
    }
    if ((this.getName() != null && this.getLocation() != null)
        || !this.getEmails().isEmpty()
        || !this.getPhones().isEmpty()
        || !this.getProfiles().isEmpty()) {
    } else {
      throw new FullContactException(
          "Invalid map request, Any of Email, Phone, SocialProfile, Name and Location must be present");
    }
    if (this.tags != null && !this.tags.isEmpty()) {
      for (Tag tag : this.tags) {
        if (!tag.isValid()) {
          throw new FullContactException("Both Key and Value must be populated for adding a Tag");
        }
      }
    }
  }

  /**
   * Method to validate request for Identity resolve. It validates that both personId and recordId
   * should not be populated
   *
   * @throws FullContactException if validation fails
   */
  public void validateForIdentityResolve() throws FullContactException {
    if (isPopulated(this.getRecordId()) && isPopulated(this.getPersonId())) {
      throw new FullContactException(
          "Both record id and person id are populated, please select one");
    }
  }

  /**
   * Method to validate request for Identity delete. It validates that recordId must be populated
   *
   * @throws FullContactException if validation fails
   */
  public void validateForIdentityDelete() throws FullContactException {
    if (!isPopulated(this.getRecordId())) {
      throw new FullContactException("recordId param must be specified");
    }
  }
}
