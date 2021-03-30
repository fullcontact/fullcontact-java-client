package com.fullcontact.apilib.models.Request;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.models.Location;
import com.fullcontact.apilib.models.PersonName;
import com.fullcontact.apilib.models.Profile;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@EqualsAndHashCode
public class MultifieldRequest {
  @With private PersonName name;
  @With private Location location;
  private String recordId, personId, partnerId, li_nonid, placekey;
  @Singular private List<String> phones, emails, maids;
  @Singular private List<Profile> profiles;

  /**
   * Check the String if it is null or empty
   *
   * @param value String value to check for non blank values
   * @return true if String is valid
   */
  protected static boolean isPopulated(String value) {
    return value != null && !value.trim().isEmpty();
  }

  public boolean isQueryable() {
    return (!this.emails.isEmpty()
        || !this.phones.isEmpty()
        || !this.profiles.isEmpty()
        || !this.maids.isEmpty()
        || isPopulated(this.recordId)
        || isPopulated(this.personId)
        || isPopulated(this.partnerId)
        || isPopulated(this.li_nonid));
  }

  /**
   * Check for minimum combinations of 'name' and 'location', if no other queryable field is present
   *
   * @throws FullContactException if 'name' and 'location' combination is not valid
   */
  public void validate() throws FullContactException {
    if (!this.isQueryable()) {
      if (location == null && name == null) {
        return;
      } else if ((location != null || placekey != null) && name != null) {
        // Validating Location fields
        if ((location != null && this.validateLocation()) || isPopulated(this.placekey)) {
          // Validating Name fields
          if (this.validateName()) {
            return;
          } else {
            throw new FullContactException("Name data requires full name or given and family name");
          }
        } else {
          throw new FullContactException(
              "A valid placekey is required or Location data requires addressLine1 and postalCode or addressLine1, city and regionCode (or region)");
        }
      }
      throw new FullContactException(
          "If you want to use 'location'(or placekey) or 'name' as an input, both must be present and they must have non-blank values");
    }
  }

  private boolean validateName() {
    return (isPopulated(name.getFull()))
        || (isPopulated(name.getGiven()) && isPopulated(name.getFamily()));
  }

  private boolean validateLocation() {
    return isPopulated(location.getAddressLine1())
        && ((isPopulated(location.getCity())
                && (isPopulated(location.getRegion()) || isPopulated(location.getRegionCode())))
            || (isPopulated(location.getPostalCode())));
  }
}
