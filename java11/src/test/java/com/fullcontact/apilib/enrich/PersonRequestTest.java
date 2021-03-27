package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.models.Location;
import com.fullcontact.apilib.models.PersonName;
import com.fullcontact.apilib.models.Profile;
import com.fullcontact.apilib.models.Request.PersonRequest;
import com.fullcontact.apilib.models.enums.Confidence;
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

public class PersonRequestTest {
  private static final Gson gson = new Gson();
  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void personRequestBuildAndSerializeTest() throws FullContactException, IOException {
    List<String> emails = new ArrayList<>();
    emails.add("test1@gmail.com");
    emails.add("test2@outlook.com");
    List<String> dataFilters = new ArrayList<>();
    dataFilters.add("social");
    dataFilters.add("individual");
    PersonRequest personRequest =
        FullContact.buildPersonRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .email("marianrd97@outlook.com")
            .phone("123-4567890")
            .emails(emails)
            .confidence(Confidence.HIGH)
            .infer(false)
            .dataFilter(dataFilters)
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
            .webhookUrl("http://www.fullcontact.com/hook")
            .recordId("customer123")
            .personId("VS1OPPPPvxHcCNPezUbvYBCDEAOdSj5AI0adsA2bLmh12345")
            .build();
    try (BufferedReader br =
        new BufferedReader(new FileReader("src/test/resources/personRequestBuildTest.txt"))) {
      String line;
      StringBuilder sb = new StringBuilder();
      while ((line = br.readLine()) != null) {
        sb.append(line.trim());
      }
      PersonRequest expectedRequest = gson.fromJson(sb.toString(), PersonRequest.class);
      Assert.assertEquals(expectedRequest, personRequest);
    }
  }

  @Test
  public void requestWithoutNameAndLocation() throws FullContactException {
    PersonRequest personRequest =
        FullContact.buildPersonRequest().email("marianrd97@outlook.com").build();
    personRequest.validate();
  }

  @Test
  public void nameWithLocationAsNullTest() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage(
        "If you want to use 'location'(or placekey) or 'name' as an input, both must be present and they must have non-blank values");
    PersonRequest personRequest =
        FullContact.buildPersonRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .build();
    personRequest.validate();
  }

  @Test
  public void nameWithPlacekeyTest() throws FullContactException {
    PersonRequest personRequest =
        FullContact.buildPersonRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .placekey("test")
            .build();
    personRequest.validate();
  }

  @Test
  public void locationWithNameAsNull() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage(
        "If you want to use 'location'(or placekey) or 'name' as an input, both must be present and they must have non-blank values");
    PersonRequest personRequest =
        FullContact.buildPersonRequest()
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
    personRequest.validate();
  }

  @Test
  public void locationWithNoAddressLine1Test() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage(
        "A valid placekey is required or Location data requires addressLine1 and postalCode or addressLine1, city and regionCode (or region)");
    PersonRequest personRequest =
        FullContact.buildPersonRequest()
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
    personRequest.validate();
  }

  @Test
  public void locationWithOnlyAddressLine1Test() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage(
        "A valid placekey is required or Location data requires addressLine1 and postalCode or addressLine1, city and regionCode (or region)");
    PersonRequest personRequest =
        FullContact.buildPersonRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(Location.builder().addressLine1("123/23").build())
            .build();
    personRequest.validate();
  }

  @Test
  public void locationWithAddressLine1AndCityTest() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage(
        "A valid placekey is required or Location data requires addressLine1 and postalCode or addressLine1, city and regionCode (or region)");
    PersonRequest personRequest =
        FullContact.buildPersonRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(Location.builder().addressLine1("123/23").city("Denver").build())
            .build();
    personRequest.validate();
  }

  @Test
  public void locationWithAddressLine1AndRegionTest() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage(
        "A valid placekey is required or Location data requires addressLine1 and postalCode or addressLine1, city and regionCode (or region)");
    PersonRequest personRequest =
        FullContact.buildPersonRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(Location.builder().addressLine1("123/23").region("Denver").build())
            .build();
    personRequest.validate();
  }

  @Test
  public void validLocation1Test() throws FullContactException {
    PersonRequest personRequest =
        FullContact.buildPersonRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(Location.builder().addressLine1("123/23").postalCode("23124").build())
            .build();
    personRequest.validate();
  }

  @Test
  public void validLocation2Test() throws FullContactException {
    PersonRequest personRequest =
        FullContact.buildPersonRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(
                Location.builder()
                    .addressLine1("123/23")
                    .addressLine2("Some Street")
                    .city("Denver")
                    .region("Denver")
                    .build())
            .build();
    personRequest.validate();
  }

  @Test
  public void validLocation3Test() throws FullContactException {

    PersonRequest personRequest =
        FullContact.buildPersonRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(
                Location.builder()
                    .addressLine1("123/23")
                    .city("Denver")
                    .regionCode("123123")
                    .build())
            .build();
    personRequest.validate();
  }

  @Test
  public void validNameTest() throws FullContactException {
    PersonRequest personRequest =
        FullContact.buildPersonRequest()
            .name(PersonName.builder().given("Marian").family("Reed").build())
            .location(Location.builder().addressLine1("123/23").postalCode("23124").build())
            .build();
    personRequest.validate();
  }

  @Test
  public void validProfileBuilder1Test() throws FullContactException {
    PersonRequest personRequest =
        FullContact.buildPersonRequest()
            .profile(Profile.builder().url("https://twitter.com/mcreedy").build())
            .build();
    personRequest.validate();
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
  public void nameWithLocationAsNullWithQueryableTest() throws FullContactException {
    PersonRequest personRequest =
        FullContact.buildPersonRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .email("test@fullcontact.com")
            .build();
    personRequest.validate();
  }

  @Test
  public void locationWithNameAsNullWithQueryableTest() throws FullContactException {
    PersonRequest personRequest =
        FullContact.buildPersonRequest()
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
    personRequest.validate();
  }

  @Test
  public void locationWithNoAddressLine1WithQueryableTest() throws FullContactException {
    PersonRequest personRequest =
        FullContact.buildPersonRequest()
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
    personRequest.validate();
  }

  @Test
  public void locationWithOnlyAddressLine1WithQueryableTest() throws FullContactException {
    PersonRequest personRequest =
        FullContact.buildPersonRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(Location.builder().addressLine1("123/23").build())
            .profile(Profile.builder().url("http://linkedin/test").build())
            .build();
    personRequest.validate();
  }

  @Test
  public void locationWithAddressLine1AndCityWithQueryableTest() throws FullContactException {
    PersonRequest personRequest =
        FullContact.buildPersonRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(Location.builder().addressLine1("123/23").city("Denver").build())
            .personId("test")
            .build();
    personRequest.validate();
  }

  @Test
  public void locationWithAddressLine1AndRegionWithQueryableTest() throws FullContactException {
    PersonRequest personRequest =
        FullContact.buildPersonRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(Location.builder().addressLine1("123/23").region("Denver").build())
            .maid("1234-sfnos-3432-sdjnwoi")
            .build();
    personRequest.validate();
  }
}
