package com.fullcontact.apilib.models.Request;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.models.Location;
import com.fullcontact.apilib.models.PersonName;
import com.fullcontact.apilib.models.Profile;
import com.fullcontact.apilib.models.Tag;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.With;

import java.util.Collections;
import java.util.List;
/** Class to create request for Resolve */
@Builder(toBuilder = true)
@Getter
public class ResolveRequest {
  @With private PersonName name;
  @With private Location location;
  private String recordId, personId, partnerId;
  @Singular private List<String> phones, emails, maids;
  @Singular private List<Profile> profiles;
  @Singular private List<Tag> tags;

  /**
   * Method to validate request for Identity map. It validates that personId should be null and must
   * contain any one of person identifier
   *
   * @throws FullContactException if validation fails
   */
  public void validateForIdentityMap() throws FullContactException {
    if (this.personId != null) {
      throw new FullContactException("Invalid map request, person id must be empty");
    }
    if ((this.name != null && this.location != null)
        || !this.emails.isEmpty()
        || !this.phones.isEmpty()
        || !this.profiles.isEmpty()) {
    } else {
      throw new FullContactException(
          "Invalid map request, Any of Email, Phone, SocialProfile, Name and Location must be present");
    }
    if (this.tags != null && !this.tags.isEmpty()) {
      for (Tag tag : this.tags) {
        if (!isPopulated(tag.getKey()) || !isPopulated(tag.getValue())) {
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
    if (isPopulated(this.recordId) && isPopulated(this.personId)) {
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
    if (!isPopulated(this.recordId)) {
      throw new FullContactException("recordId param must be specified");
    }
  }

  /**
   * Check the String if it is null or empty
   *
   * @param value String value to check for non blank values
   * @return true if String is valid
   */
  protected static boolean isPopulated(String value) {
    return value != null && !value.trim().isEmpty();
  }

  public static class ResolveRequestBuilder {

    public ResolveRequest build() throws FullContactException {
      List<String> phones =
          this.phones != null ? Collections.unmodifiableList(this.phones) : Collections.emptyList();
      List<String> emails =
          this.emails != null ? Collections.unmodifiableList(this.emails) : Collections.emptyList();
      List<String> maids =
          this.maids != null ? Collections.unmodifiableList((this.maids)) : Collections.emptyList();
      List<Profile> profiles =
          this.profiles != null
              ? Collections.unmodifiableList(this.profiles)
              : Collections.emptyList();
      List<Tag> tags =
          this.tags != null ? Collections.unmodifiableList((this.tags)) : Collections.emptyList();
      this.validate();
      return new ResolveRequest(
          name, location, recordId, personId, partnerId, phones, emails, maids, profiles, tags);
    }

    /**
     * Check for minimum combinations of 'name' and 'location', if present
     *
     * @throws FullContactException if 'name' and 'location' combination is not valid
     */
    public void validate() throws FullContactException {
      if (location == null && name == null) {
        return;
      } else if (location != null && name != null) {
        // Validating Location fields
        if (isPopulated(location.getAddressLine1())
            && ((isPopulated(location.getCity())
                    && (isPopulated(location.getRegion()) || isPopulated(location.getRegionCode())))
                || (isPopulated(location.getPostalCode())))) {
          // Validating Name fields
          if ((isPopulated(name.getFull()))
              || (isPopulated(name.getGiven()) && isPopulated(name.getFamily()))) {
            return;
          } else {
            throw new FullContactException("Name data requires full name or given and family name");
          }
        } else {
          throw new FullContactException(
              "Location data requires addressLine1 and postalCode or addressLine1, city and regionCode (or region)");
        }
      }
      throw new FullContactException(
          "If you want to use 'location' or 'name' as an input, both must be present and they must have non-blank values");
    }
  }
}
