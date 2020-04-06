package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.models.Location;
import com.fullcontact.apilib.models.PersonName;
import com.fullcontact.apilib.models.Profile;
import com.fullcontact.apilib.models.Request.PersonRequest;
import com.fullcontact.apilib.models.enums.Confidence;
import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PersonRequestTest {
  private static final Gson gson = new Gson();

  @Test
  public void personRequestBuildAndSerializeTest() throws FullContactException, IOException {
    List<String> emails = new ArrayList<>();
    emails.add("test1@gmail.com");
    emails.add("test2@outlook.com");
    PersonRequest personRequest =
        FullContact.buildPersonRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .email("marianrd97@outlook.com")
            .phone("123-4567890")
            .emails(emails)
            .confidence(Confidence.HIGH)
            .infer(false)
            .dataFilter("individual")
            .dataFilter("social")
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
            .build();
    try (BufferedReader br =
        new BufferedReader(new FileReader("src/test/resources/personRequestBuildTest.txt"))) {
      String line;
      StringBuilder sb = new StringBuilder();
      while ((line = br.readLine()) != null) {
        sb.append(line.trim());
      }
      Assert.assertEquals(sb.toString(), gson.toJson(personRequest));
    }
  }

  @Test
  public void requestWithoutNameAndLocation() throws FullContactException {
    PersonRequest personRequest =
        FullContact.buildPersonRequest().email("marianrd97@outlook.com").build();
  }

  @Test
  public void nameWithLocationAsNullTest() {
    try {
      PersonRequest personRequest =
          FullContact.buildPersonRequest()
              .name(PersonName.builder().full("Marian C Reed").build())
              .build();
    } catch (FullContactException fce) {
      Assert.assertEquals(
          "If you want to use 'location' or 'name' as an input, both must be present and they must have non-blank values",
          fce.getMessage());
    }
  }

  @Test
  public void locationWithNameAsNull() {
    try {
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
    } catch (FullContactException fce) {
      Assert.assertEquals(
          "If you want to use 'location' or 'name' as an input, both must be present and they must have non-blank values",
          fce.getMessage());
    }
  }

  @Test
  public void locationWithNoAddressLine1Test() {
    try {
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
    } catch (FullContactException fce) {
      Assert.assertEquals(
          "Location data requires addressLine1 and postalCode or addressLine1, city and regionCode (or region)",
          fce.getMessage());
    }
  }

  @Test
  public void locationWithOnlyAddressLine1Test() {
    try {
      PersonRequest personRequest =
          FullContact.buildPersonRequest()
              .name(PersonName.builder().full("Marian C Reed").build())
              .location(Location.builder().addressLine1("123/23").build())
              .build();
    } catch (FullContactException fce) {
      Assert.assertEquals(
          "Location data requires addressLine1 and postalCode or addressLine1, city and regionCode (or region)",
          fce.getMessage());
    }
  }

  @Test
  public void locationWithAddressLine1AndCityTest() {
    try {
      PersonRequest personRequest =
          FullContact.buildPersonRequest()
              .name(PersonName.builder().full("Marian C Reed").build())
              .location(Location.builder().addressLine1("123/23").city("Denver").build())
              .build();
    } catch (FullContactException fce) {
      Assert.assertEquals(
          "Location data requires addressLine1 and postalCode or addressLine1, city and regionCode (or region)",
          fce.getMessage());
    }
  }

  @Test
  public void locationWithAddressLine1AndRegionTest() {
    try {
      PersonRequest personRequest =
          FullContact.buildPersonRequest()
              .name(PersonName.builder().full("Marian C Reed").build())
              .location(Location.builder().addressLine1("123/23").region("Denver").build())
              .build();
    } catch (FullContactException fce) {
      Assert.assertEquals(
          "Location data requires addressLine1 and postalCode or addressLine1, city and regionCode (or region)",
          fce.getMessage());
    }
  }

  @Test
  public void validLocation1Test() throws FullContactException {
    PersonRequest personRequest =
        FullContact.buildPersonRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(Location.builder().addressLine1("123/23").postalCode("23124").build())
            .build();
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
  }

  @Test
  public void validNameTest() throws FullContactException {
    PersonRequest personRequest =
        FullContact.buildPersonRequest()
            .name(PersonName.builder().given("Marian").family("Reed").build())
            .location(Location.builder().addressLine1("123/23").postalCode("23124").build())
            .build();
  }

  @Test
  public void validProfileBuilder1Test() throws FullContactException {
    PersonRequest personRequest =
        FullContact.buildPersonRequest()
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
}
