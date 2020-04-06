package com.fullcontact.apilib.models.Response;

import com.fullcontact.apilib.models.Location;
import lombok.*;

import java.util.List;
import java.util.Optional;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class PersonResponse {
  private String email,
      twitter,
      phone,
      fullName,
      ageRange,
      gender,
      location,
      title,
      organization,
      linkedin,
      bio,
      avatar,
      website;
  private Details details;
  public boolean isSuccessful;
  public int statusCode;
  public String message;

  public Optional<Details> getDetails() {
    return Optional.ofNullable(this.details);
  }

  public Optional<Integer> getAge() {
    return this.getDetails().map(Details::getAge).map(Age::getValue);
  }

  public Optional<Date> getBirthday() {
    return this.getDetails().map(Details::getAge).map(Age::getBirthday);
  }

  public Optional<Finance> getFinance() {
    return this.getDetails().map(Details::getFinance);
  }

  public Optional<LocationInfo> getLocationInfo() {
    return this.getDetails().map(Details::getHousehold).map(Household::getLocationInfo);
  }

  public Optional<FamilyInfo> getFamilyInfo() {
    return this.getDetails().map(Details::getHousehold).map(Household::getFamilyInfo);
  }

  public Optional<Demographics> getDemographics() {
    return this.getDetails().map(Details::getDemographics);
  }

  public Optional<List<EmailAndPhone>> getEmailsList() {
    return this.getDetails().map(Details::getEmails);
  }

  public Optional<List<EmailAndPhone>> getPhonesList() {
    return this.getDetails().map(Details::getPhones);
  }

  public Optional<List<Location>> getLocationsList() {
    return this.getDetails().map(Details::getLocations);
  }

  public Optional<List<Employment>> getEmploymentList() {
    return this.getDetails().map(Details::getEmployment);
  }

  public Optional<List<Photo>> getPhototsList() {
    return this.getDetails().map(Details::getPhotos);
  }

  public Optional<List<Education>> getEducationList() {
    return this.getDetails().map(Details::getEducation);
  }

  public Optional<List<URL>> getUrlList() {
    return this.getDetails().map(Details::getUrls);
  }

  public Optional<List<Interest>> getInterestList() {
    return this.getDetails().map(Details::getInterests);
  }

  public Optional<PurchaseBehavior> getBuyerCatalog() {
    return this.getDetails().map(Details::getBuyer).map(Buyer::getCatalog);
  }

  public Optional<PurchaseBehavior> getBuyerRetail() {
    return this.getDetails().map(Details::getBuyer).map(Buyer::getRetail);
  }

  public Optional<Apparel> getBuyerCatalogApparel() {
    return this.getDetails()
        .map(Details::getBuyer)
        .map(Buyer::getCatalog)
        .map(PurchaseBehavior::getApparel);
  }

  public Optional<Apparel> getBuyerRetailApparel() {
    return this.getDetails()
        .map(Details::getBuyer)
        .map(Buyer::getRetail)
        .map(PurchaseBehavior::getApparel);
  }

  public Optional<Payment> getBuyerCatalogPayment() {
    return this.getDetails()
        .map(Details::getBuyer)
        .map(Buyer::getCatalog)
        .map(PurchaseBehavior::getPayment);
  }

  public Optional<Payment> getBuyerRetailPayment() {
    return this.getDetails()
        .map(Details::getBuyer)
        .map(Buyer::getRetail)
        .map(PurchaseBehavior::getPayment);
  }

  public Optional<Presence> getHouseHoldPresence() {
    return this.getDetails().map(Details::getHousehold).map(Household::getPresence);
  }

  public Optional<HomeInfo> getHomeInfo() {
    return this.getDetails().map(Details::getHousehold).map(Household::getHomeInfo);
  }

  public Optional<ProfileData> getTwitterObject() {
    return this.getDetails().map(Details::getProfiles).map(Profiles::getTwitter);
  }

  public Optional<ProfileData> getLinkedinObject() {
    return this.getDetails().map(Details::getProfiles).map(Profiles::getLinkedin);
  }
}
