package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.models.Location;
import com.fullcontact.apilib.models.PersonName;
import com.fullcontact.apilib.models.Profile;
import com.fullcontact.apilib.models.Request.ResolveRequest;
import com.fullcontact.apilib.models.Tag;
import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResolveRequestBuildTest {
  private static final Gson gson = new Gson();

  @Test
  public void resolveRequestBuildAndSerializeTest() throws FullContactException, IOException {
    List<String> emails = new ArrayList<>();
    emails.add("test1@gmail.com");
    emails.add("test2@outlook.com");
    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .email("marianrd97@outlook.com")
            .phone("123-4567890")
            .emails(emails)
            .location(
                Location.builder()
                    .addressLine1("123/23")
                    .addressLine2("Some Street")
                    .city("Denver")
                    .region("Denver")
                    .regionCode("123123")
                    .postalCode("23124")
                    .build())
            .profile(Profile.builder().url("https://twitter.com/mcreedy").build())
            .profile(Profile.builder().url("https://twitter.com/mcreedytest").build())
            .maid("abcd-123-abcd-1234-abcdlkjhasdfgh")
            .maid("1234-snbk-lkldiemvmruixp-2kdp-vdm")
            .recordId("customer123")
            .personId("VS1OPPPPvxHcCNPezUbvYBCDEAOdSj5AI0adsA2bLmh12345")
            .build();
    try (BufferedReader br =
        new BufferedReader(new FileReader("src/test/resources/resolveRequestBuildTest.txt"))) {
      String line;
      StringBuilder sb = new StringBuilder();
      while ((line = br.readLine()) != null) {
        sb.append(line.trim());
      }
      ResolveRequest expectedRequest = gson.fromJson(sb.toString(), ResolveRequest.class);
      Assert.assertEquals(expectedRequest, resolveRequest);
    }
  }

  @Test
  public void requestWithoutNameAndLocation() throws FullContactException {
    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest().email("marianrd97@outlook.com").build();
    resolveRequest.validate();
  }

  @Test
  public void nameWithLocationAsNullTest() {
    try {
      ResolveRequest resolveRequest =
          FullContact.buildResolveRequest()
              .name(PersonName.builder().full("Marian C Reed").build())
              .build();
      resolveRequest.validate();
    } catch (FullContactException fce) {
      Assert.assertEquals(
          "If you want to use 'location' or 'name' as an input, both must be present and they must have non-blank values",
          fce.getMessage());
    }
  }

  @Test
  public void locationWithNameAsNull() {
    try {
      ResolveRequest resolveRequest =
          FullContact.buildResolveRequest()
              .location(
                  Location.builder()
                      .addressLine1("123/23")
                      .addressLine2("Some Street")
                      .city("Denver")
                      .region("Denver")
                      .regionCode("123123")
                      .postalCode("23124")
                      .build())
              .build();
      resolveRequest.validate();
    } catch (FullContactException fce) {
      Assert.assertEquals(
          "If you want to use 'location' or 'name' as an input, both must be present and they must have non-blank values",
          fce.getMessage());
    }
  }

  @Test
  public void locationWithNoAddressLine1Test() {
    try {
      ResolveRequest resolveRequest =
          FullContact.buildResolveRequest()
              .name(PersonName.builder().full("Marian C Reed").build())
              .location(
                  Location.builder()
                      .addressLine2("Some Street")
                      .city("Denver")
                      .region("Denver")
                      .regionCode("123123")
                      .postalCode("23124")
                      .build())
              .build();
      resolveRequest.validate();
    } catch (FullContactException fce) {
      Assert.assertEquals(
          "Location data requires addressLine1 and postalCode or addressLine1, city and regionCode (or region)",
          fce.getMessage());
    }
  }

  @Test
  public void locationWithOnlyAddressLine1Test() {
    try {
      ResolveRequest resolveRequest =
          FullContact.buildResolveRequest()
              .name(PersonName.builder().full("Marian C Reed").build())
              .location(Location.builder().addressLine1("123/23").build())
              .build();
      resolveRequest.validate();
    } catch (FullContactException fce) {
      Assert.assertEquals(
          "Location data requires addressLine1 and postalCode or addressLine1, city and regionCode (or region)",
          fce.getMessage());
    }
  }

  @Test
  public void locationWithAddressLine1AndCityTest() {
    try {
      ResolveRequest resolveRequest =
          FullContact.buildResolveRequest()
              .name(PersonName.builder().full("Marian C Reed").build())
              .location(Location.builder().addressLine1("123/23").city("Denver").build())
              .build();
      resolveRequest.validate();
    } catch (FullContactException fce) {
      Assert.assertEquals(
          "Location data requires addressLine1 and postalCode or addressLine1, city and regionCode (or region)",
          fce.getMessage());
    }
  }

  @Test
  public void locationWithAddressLine1AndRegionTest() {
    try {
      ResolveRequest resolveRequest =
          FullContact.buildResolveRequest()
              .name(PersonName.builder().full("Marian C Reed").build())
              .location(Location.builder().addressLine1("123/23").region("Denver").build())
              .build();
      resolveRequest.validate();
    } catch (FullContactException fce) {
      Assert.assertEquals(
          "Location data requires addressLine1 and postalCode or addressLine1, city and regionCode (or region)",
          fce.getMessage());
    }
  }

  @Test
  public void validLocation1Test() throws FullContactException {
    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(Location.builder().addressLine1("123/23").postalCode("23124").build())
            .build();
  }

  @Test
  public void validLocation2Test() throws FullContactException {
    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(
                Location.builder()
                    .addressLine1("123/23")
                    .addressLine2("Some Street")
                    .city("Denver")
                    .region("Denver")
                    .build())
            .build();
  }

  @Test
  public void validLocation3Test() throws FullContactException {
    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(
                Location.builder()
                    .addressLine1("123/23")
                    .city("Denver")
                    .regionCode("123123")
                    .build())
            .build();
  }

  @Test
  public void validNameTest() throws FullContactException {
    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest()
            .name(PersonName.builder().given("Marian").family("Reed").build())
            .location(Location.builder().addressLine1("123/23").postalCode("23124").build())
            .build();
  }

  @Test
  public void validProfileBuilder1Test() throws FullContactException {
    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest()
            .profile(Profile.builder().url("https://twitter.com/mcreedy").build())
            .build();
  }

  @Test
  public void validProfileBuilder2Test() throws FullContactException {
    Profile profile = Profile.builder().service("twitter").url("mcreedy").build();
  }

  @Test
  public void validProfileBuilder3Test() throws FullContactException {
    Profile profile = Profile.builder().service("twitter").username("mcreedy").build();
  }

  @Test
  public void profileWithUrlAndUserid() {
    try {
      Profile profile =
          Profile.builder().url("https://twitter.com/mcreedy").userid("mcreedy").build();
    } catch (FullContactException fce) {
      Assert.assertEquals(
          "Specifying username or userid together with url is not allowed", fce.getMessage());
    }
  }

  @Test
  public void profileWithUrlAndUsername() {
    try {
      Profile profile =
          Profile.builder().url("https://twitter.com/mcreedy").username("mcreedy").build();
    } catch (FullContactException fce) {
      Assert.assertEquals(
          "Specifying username or userid together with url is not allowed", fce.getMessage());
    }
  }

  @Test
  public void profileWithOnlyService() {
    try {
      Profile profile = Profile.builder().service("twitter").build();
    } catch (FullContactException fce) {
      Assert.assertEquals(
          "Either url or service plus username or userid must be set on every profiles entry.",
          fce.getMessage());
    }
  }

  @Test
  public void profileWithServiceAndUseridAndUsername() {
    try {
      Profile profile =
          Profile.builder().service("twitter").userid("mcreedy").userid("mcreedy").build();
    } catch (FullContactException fce) {
      Assert.assertEquals(
          "Specifying userid together with username is not allowed", fce.getMessage());
    }
  }

  @Test
  public void identityMapRequestWithPersonId() {
    try {
      ResolveRequest resolveRequest =
          FullContact.buildResolveRequest().email("test").personId("test").build();
      resolveRequest.validateForIdentityMap();
    } catch (FullContactException fce) {
      Assert.assertEquals("Invalid map request, person id must be empty", fce.getMessage());
    }
  }

  @Test
  public void identityMapRequestWithOnlyRecordId() {
    try {
      ResolveRequest resolveRequest = FullContact.buildResolveRequest().recordId("test").build();
      resolveRequest.validateForIdentityMap();
    } catch (FullContactException fce) {
      Assert.assertEquals(
          "Invalid map request, Any of Email, Phone, SocialProfile, Name and Location must be present",
          fce.getMessage());
    }
  }

  @Test
  public void identityResolveRequestWithRecordIdAndPersonId() {
    try {
      ResolveRequest resolveRequest =
          FullContact.buildResolveRequest().recordId("test").personId("test").build();
      resolveRequest.validateForIdentityResolve();
    } catch (FullContactException fce) {
      Assert.assertEquals(
          "Both record id and person id are populated, please select one", fce.getMessage());
    }
  }

  @Test
  public void identityDeleteRequestWithNoRecordId() {
    try {
      ResolveRequest resolveRequest = FullContact.buildResolveRequest().personId("test").build();
      resolveRequest.validateForIdentityDelete();
    } catch (FullContactException fce) {
      Assert.assertEquals("recordId param must be specified", fce.getMessage());
    }
  }

  @Test
  public void identityMapWithTags() throws FullContactException {
    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest()
            .recordId("test")
            .email("test")
            .tag(Tag.builder().key("key").value("value").build())
            .build();
    resolveRequest.validateForIdentityMap();
  }

  @Test
  public void identityMapWithInvalidTags() {
    try {
      ResolveRequest resolveRequest =
          FullContact.buildResolveRequest()
              .recordId("test")
              .email("test")
              .tag(Tag.builder().key("key").build())
              .build();
      resolveRequest.validateForIdentityMap();
    } catch (FullContactException fce) {
      Assert.assertEquals(
          "Both Key and Value must be populated for adding a Tag", fce.getMessage());
    }
  }
}
