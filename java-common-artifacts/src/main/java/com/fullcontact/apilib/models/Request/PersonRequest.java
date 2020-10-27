package com.fullcontact.apilib.models.Request;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.models.Location;
import com.fullcontact.apilib.models.PersonName;
import com.fullcontact.apilib.models.Profile;
import com.fullcontact.apilib.models.enums.Confidence;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.With;

import java.util.Collections;
import java.util.List;

/** Class to create request for Person Enrich */
@Builder(toBuilder = true)
@Getter
public class PersonRequest {
  @With private PersonName name;
  @With private Location location;
  private String webhookUrl, recordId, personId, li_nonid;
  private Confidence confidence;
  private boolean infer;
  @Singular private List<String> phones, emails, dataFilters, maids;
  @Singular private List<Profile> profiles;

  public static class PersonRequestBuilder {
    private boolean infer = true;

    public PersonRequest build() throws FullContactException {
      List<String> phones =
          this.phones != null ? Collections.unmodifiableList(this.phones) : Collections.emptyList();
      List<String> emails =
          this.emails != null ? Collections.unmodifiableList(this.emails) : Collections.emptyList();
      List<String> dataFilters =
          this.dataFilters != null
              ? Collections.unmodifiableList((this.dataFilters))
              : Collections.emptyList();
      List<String> maids =
          this.maids != null ? Collections.unmodifiableList((this.maids)) : Collections.emptyList();
      List<Profile> profiles =
          this.profiles != null
              ? Collections.unmodifiableList(this.profiles)
              : Collections.emptyList();
      this.validate();
      return new PersonRequest(
          name,
          location,
          webhookUrl,
          recordId,
          personId,
          li_nonid,
          confidence,
          infer,
          phones,
          emails,
          dataFilters,
          maids,
          profiles);
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
