package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.models.Location;
import com.fullcontact.apilib.models.PersonName;
import com.fullcontact.apilib.models.Profile;
import com.fullcontact.apilib.models.Request.ResolveRequest;
import com.fullcontact.apilib.models.Tag;
import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResolveRequestBuildTest {
  private static final Gson gson = new Gson();
  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void personRequestBuildAndSerializeTest() throws FullContactException, IOException {
    List<String> emails = new ArrayList<>();
    emails.add("test1@gmail.com");
    emails.add("test2@outlook.com");
    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .email("marianrd97@outlook.com")
            .phone("123-4567890")
            .emails(emails)
            .placekey("123-456@5s9-qns-bfp")
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
            .generatePid(true)
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
  public void nameWithLocationAsNullTest() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage(
        "If you want to use 'location'(or placekey) or 'name' as an input, both must be present and they must have non-blank values");
    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .build();
    resolveRequest.validate();
  }

  @Test
  public void nameWithPlacekeyTest() throws FullContactException {
    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .placekey("test")
            .build();
    resolveRequest.validate();
  }

  @Test
  public void locationWithNameAsNull() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage(
        "If you want to use 'location'(or placekey) or 'name' as an input, both must be present and they must have non-blank values");
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
  }

  @Test
  public void locationWithNoAddressLine1Test() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage(
        "Location data requires addressLine1 and postalCode or addressLine1, city and regionCode (or region)");
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
  }

  @Test
  public void locationWithOnlyAddressLine1Test() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage(
        "Location data requires addressLine1 and postalCode or addressLine1, city and regionCode (or region)");
    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(Location.builder().addressLine1("123/23").build())
            .build();
    resolveRequest.validate();
  }

  @Test
  public void locationWithAddressLine1AndCityTest() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage(
        "Location data requires addressLine1 and postalCode or addressLine1, city and regionCode (or region)");
    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(Location.builder().addressLine1("123/23").city("Denver").build())
            .build();
    resolveRequest.validate();
  }

  @Test
  public void locationWithAddressLine1AndRegionTest() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage(
        "Location data requires addressLine1 and postalCode or addressLine1, city and regionCode (or region)");
    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(Location.builder().addressLine1("123/23").region("Denver").build())
            .build();
    resolveRequest.validate();
  }

  @Test
  public void validLocation1Test() throws FullContactException {
    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(Location.builder().addressLine1("123/23").postalCode("23124").build())
            .build();
    resolveRequest.validate();
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
    resolveRequest.validate();
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
    resolveRequest.validate();
  }

  @Test
  public void validNameTest() throws FullContactException {
    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest()
            .name(PersonName.builder().given("Marian").family("Reed").build())
            .location(Location.builder().addressLine1("123/23").postalCode("23124").build())
            .build();
    resolveRequest.validate();
  }

  @Test
  public void validProfileBuilder1Test() throws FullContactException {
    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest()
            .profile(Profile.builder().url("https://twitter.com/mcreedy").build())
            .build();
    resolveRequest.validate();
  }

  @Test
  public void validProfileBuilder2Test() throws FullContactException {
    Profile.builder().service("twitter").url("mcreedy").build();
  }

  @Test
  public void validProfileBuilder3Test() throws FullContactException {
    Profile.builder().service("twitter").username("mcreedy").build();
  }

  @Test
  public void profileWithUrlAndUserid() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage("Specifying username or userid together with url is not allowed");
    Profile.builder().url("https://twitter.com/mcreedy").userid("mcreedy").build();
  }

  @Test
  public void profileWithUrlAndUsername() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage("Specifying username or userid together with url is not allowed");
    Profile.builder().url("https://twitter.com/mcreedy").username("mcreedy").build();
  }

  @Test
  public void profileWithOnlyService() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage(
        "Either url or service plus username or userid must be set on every profiles entry.");
    Profile.builder().service("twitter").build();
  }

  @Test
  public void profileWithServiceAndUseridAndUsername() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage("Specifying userid together with username is not allowed");
    Profile.builder().service("twitter").userid("mcreedy").username("mcreedy").build();
  }

  @Test
  public void identityMapRequestWithPersonId() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage("Invalid map request, person id must be empty");
    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest().email("test").personId("test").build();
    resolveRequest.validateForIdentityMap();
  }

  @Test
  public void identityMapRequestWithOnlyRecordId() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage(
        "Invalid map request, Any of Email, Phone, SocialProfile, Name and Location must be present");
    ResolveRequest resolveRequest = FullContact.buildResolveRequest().recordId("test").build();
    resolveRequest.validateForIdentityMap();
  }

  @Test
  public void identityResolveRequestWithRecordIdAndPersonId() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage("Both record id and person id are populated, please select one");
    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest().recordId("test").personId("test").build();
    resolveRequest.validateForIdentityResolve();
  }

  @Test
  public void identityDeleteRequestWithNoRecordId() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage("recordId param must be specified");
    ResolveRequest resolveRequest = FullContact.buildResolveRequest().personId("test").build();
    resolveRequest.validateForIdentityDelete();
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
  public void identityMapWithInvalidTags() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage("Both Key and Value must be populated for adding a Tag");
    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest()
            .recordId("test")
            .email("test")
            .tag(Tag.builder().key("key").build())
            .build();
    resolveRequest.validateForIdentityMap();
  }

  @Test
  public void nameWithLocationAsNullWithQueryableTest() throws FullContactException {
    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .email("test@fullcontact.com")
            .build();
    resolveRequest.validate();
  }

  @Test
  public void locationWithNameAsNullWithQueryableTest() throws FullContactException {
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
            .phone("1234567")
            .build();
    resolveRequest.validate();
  }

  @Test
  public void locationWithNoAddressLine1WithQueryableTest() throws FullContactException {
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
            .recordId("r1")
            .build();
    resolveRequest.validate();
  }

  @Test
  public void locationWithOnlyAddressLine1WithQueryableTest() throws FullContactException {
    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(Location.builder().addressLine1("123/23").build())
            .maid("12334-sdnosf-23423-sldfsi")
            .build();
    resolveRequest.validate();
  }

  @Test
  public void locationWithAddressLine1AndCityWithQueryableTest() throws FullContactException {
    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(Location.builder().addressLine1("123/23").city("Denver").build())
            .profile(Profile.builder().url("http://linkedin/test").build())
            .build();
    resolveRequest.validate();
  }

  @Test
  public void locationWithAddressLine1AndRegionWithQueryableTest() throws FullContactException {
    ResolveRequest resolveRequest =
        FullContact.buildResolveRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(Location.builder().addressLine1("123/23").region("Denver").build())
            .li_nonid("3iruhow1039oijwe")
            .build();
    resolveRequest.validate();
  }
}
