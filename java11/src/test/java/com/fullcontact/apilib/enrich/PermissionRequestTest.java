package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.models.Location;
import com.fullcontact.apilib.models.PersonName;
import com.fullcontact.apilib.models.Profile;
import com.fullcontact.apilib.models.Request.*;
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

public class PermissionRequestTest {
  private static final Gson gson = new Gson();
  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void permissionRequestBuildAndSerializeTest() throws FullContactException, IOException {
    List<String> emails = new ArrayList<>();
    emails.add("test1@gmail.com");
    emails.add("test2@outlook.com");
    MultifieldRequest query =
        FullContact.buildMultifieldRequest()
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
            .li_nonid("u3odijoewifij309okjdowiedpopokpo3ri")
            .build();

    List<PurposeRequest> purposeRequestList =
        List.of(
            PurposeRequest.builder()
                .purposeId(2)
                .channel(List.of("web", "phone", "mobile", "offline", "email"))
                .enabled(true)
                .ttl(365)
                .build(),
            PurposeRequest.builder()
                .purposeId(3)
                .channel(List.of("web", "phone", "mobile", "offline", "email"))
                .enabled(true)
                .ttl(365)
                .build());
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(query)
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(1)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .enabled(true)
                    .ttl(365)
                    .build())
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(4)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .enabled(true)
                    .ttl(365)
                    .build())
            .consentPurposes(purposeRequestList)
            .policyUrl("https://policy.fullcontact.com/test")
            .termsService("https://terms.fullcontact.com/test")
            .collectionMethod("tag")
            .collectionLocation("US")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    try (BufferedReader br =
        new BufferedReader(new FileReader("src/test/resources/permissionRequestBuild.txt"))) {
      String line;
      StringBuilder sb = new StringBuilder();
      while ((line = br.readLine()) != null) {
        sb.append(line.trim());
      }
      PermissionRequest expectedRequest = gson.fromJson(sb.toString(), PermissionRequest.class);
      Assert.assertEquals(expectedRequest, permissionRequest);
    }
  }

  @Test
  public void channelPurposeRequestBuildAndSerializeTest()
      throws FullContactException, IOException {
    List<String> emails = new ArrayList<>();
    emails.add("test1@gmail.com");
    emails.add("test2@outlook.com");
    MultifieldRequest query =
        FullContact.buildMultifieldRequest()
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
            .li_nonid("u3odijoewifij309okjdowiedpopokpo3ri")
            .build();

    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest().query(query).purposeId(1).channel("web").build();
    try (BufferedReader br =
        new BufferedReader(new FileReader("src/test/resources/channelPurposeRequestBuild.txt"))) {
      String line;
      StringBuilder sb = new StringBuilder();
      while ((line = br.readLine()) != null) {
        sb.append(line.trim());
      }
      ChannelPurposeRequest expectedRequest =
          gson.fromJson(sb.toString(), ChannelPurposeRequest.class);
      Assert.assertEquals(expectedRequest, channelPurposeRequest);
    }
  }

  // Standard tests for MultiField query -- Start

  @Test
  public void nameWithLocationAsNullTest() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage(
        "If you want to use 'location' or 'name' as an input, both must be present and they must have non-blank values");
    MultifieldRequest query =
        FullContact.buildMultifieldRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .build();
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(query)
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(1)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .enabled(true)
                    .ttl(365)
                    .build())
            .policyUrl("https://policy.fullcontact.com/test")
            .termsService("https://terms.fullcontact.com/test")
            .collectionMethod("tag")
            .collectionLocation("US")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    permissionRequest.validate();

    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest().query(query).purposeId(1).channel("web").build();
    channelPurposeRequest.validate();
  }

  @Test
  public void locationWithNameAsNull() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage(
        "If you want to use 'location' or 'name' as an input, both must be present and they must have non-blank values");
    MultifieldRequest query =
        FullContact.buildMultifieldRequest()
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
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(query)
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(1)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .enabled(true)
                    .ttl(365)
                    .build())
            .policyUrl("https://policy.fullcontact.com/test")
            .termsService("https://terms.fullcontact.com/test")
            .collectionMethod("tag")
            .collectionLocation("US")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    permissionRequest.validate();

    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest().query(query).purposeId(1).channel("web").build();
    channelPurposeRequest.validate();
  }

  @Test
  public void locationWithNoAddressLine1Test() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage(
        "Location data requires addressLine1 and postalCode or addressLine1, city and regionCode (or region)");
    MultifieldRequest query =
        FullContact.buildMultifieldRequest()
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
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(query)
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(1)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .enabled(true)
                    .ttl(365)
                    .build())
            .policyUrl("https://policy.fullcontact.com/test")
            .termsService("https://terms.fullcontact.com/test")
            .collectionMethod("tag")
            .collectionLocation("US")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    permissionRequest.validate();

    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest().query(query).purposeId(1).channel("web").build();
    channelPurposeRequest.validate();
  }

  @Test
  public void locationWithOnlyAddressLine1Test() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage(
        "Location data requires addressLine1 and postalCode or addressLine1, city and regionCode (or region)");
    MultifieldRequest query =
        FullContact.buildMultifieldRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(Location.builder().addressLine1("123/23").build())
            .build();
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(query)
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(1)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .enabled(true)
                    .ttl(365)
                    .build())
            .policyUrl("https://policy.fullcontact.com/test")
            .termsService("https://terms.fullcontact.com/test")
            .collectionMethod("tag")
            .collectionLocation("US")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    permissionRequest.validate();

    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest().query(query).purposeId(1).channel("web").build();
    channelPurposeRequest.validate();
  }

  @Test
  public void locationWithAddressLine1AndCityTest() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage(
        "Location data requires addressLine1 and postalCode or addressLine1, city and regionCode (or region)");
    MultifieldRequest query =
        FullContact.buildMultifieldRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(Location.builder().addressLine1("123/23").city("Denver").build())
            .build();
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(query)
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(1)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .enabled(true)
                    .ttl(365)
                    .build())
            .policyUrl("https://policy.fullcontact.com/test")
            .termsService("https://terms.fullcontact.com/test")
            .collectionMethod("tag")
            .collectionLocation("US")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    permissionRequest.validate();

    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest().query(query).purposeId(1).channel("web").build();
    channelPurposeRequest.validate();
  }

  @Test
  public void locationWithAddressLine1AndRegionTest() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage(
        "Location data requires addressLine1 and postalCode or addressLine1, city and regionCode (or region)");
    MultifieldRequest query =
        FullContact.buildMultifieldRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(Location.builder().addressLine1("123/23").region("Denver").build())
            .build();
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(query)
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(1)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .enabled(true)
                    .ttl(365)
                    .build())
            .policyUrl("https://policy.fullcontact.com/test")
            .termsService("https://terms.fullcontact.com/test")
            .collectionMethod("tag")
            .collectionLocation("US")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    permissionRequest.validate();

    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest().query(query).purposeId(1).channel("web").build();
    channelPurposeRequest.validate();
  }

  @Test
  public void validLocation1Test() throws FullContactException {
    MultifieldRequest query =
        FullContact.buildMultifieldRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(Location.builder().addressLine1("123/23").postalCode("23124").build())
            .build();
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(query)
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(1)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .enabled(true)
                    .ttl(365)
                    .build())
            .policyUrl("https://policy.fullcontact.com/test")
            .termsService("https://terms.fullcontact.com/test")
            .collectionMethod("tag")
            .collectionLocation("US")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    permissionRequest.validate();

    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest().query(query).purposeId(1).channel("web").build();
    channelPurposeRequest.validate();
  }

  @Test
  public void validLocation2Test() throws FullContactException {
    MultifieldRequest query =
        FullContact.buildMultifieldRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(
                Location.builder()
                    .addressLine1("123/23")
                    .addressLine2("Some Street")
                    .city("Denver")
                    .region("Denver")
                    .build())
            .build();
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(query)
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(1)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .enabled(true)
                    .ttl(365)
                    .build())
            .policyUrl("https://policy.fullcontact.com/test")
            .termsService("https://terms.fullcontact.com/test")
            .collectionMethod("tag")
            .collectionLocation("US")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    permissionRequest.validate();

    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest().query(query).purposeId(1).channel("web").build();
    channelPurposeRequest.validate();
  }

  @Test
  public void validLocation3Test() throws FullContactException {

    MultifieldRequest query =
        FullContact.buildMultifieldRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(
                Location.builder()
                    .addressLine1("123/23")
                    .city("Denver")
                    .regionCode("123123")
                    .build())
            .build();
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(query)
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(1)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .enabled(true)
                    .ttl(365)
                    .build())
            .policyUrl("https://policy.fullcontact.com/test")
            .termsService("https://terms.fullcontact.com/test")
            .collectionMethod("tag")
            .collectionLocation("US")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    permissionRequest.validate();

    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest().query(query).purposeId(1).channel("web").build();
    channelPurposeRequest.validate();
  }

  @Test
  public void validNameTest() throws FullContactException {
    MultifieldRequest query =
        FullContact.buildMultifieldRequest()
            .name(PersonName.builder().given("Marian").family("Reed").build())
            .location(Location.builder().addressLine1("123/23").postalCode("23124").build())
            .build();
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(query)
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(1)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .enabled(true)
                    .ttl(365)
                    .build())
            .policyUrl("https://policy.fullcontact.com/test")
            .termsService("https://terms.fullcontact.com/test")
            .collectionMethod("tag")
            .collectionLocation("US")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    permissionRequest.validate();

    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest().query(query).purposeId(1).channel("web").build();
    channelPurposeRequest.validate();
  }

  @Test
  public void validProfileBuilder1Test() throws FullContactException {
    MultifieldRequest query =
        FullContact.buildMultifieldRequest()
            .profile(Profile.builder().url("https://twitter.com/mcreedy").build())
            .build();
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(query)
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(1)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .enabled(true)
                    .ttl(365)
                    .build())
            .policyUrl("https://policy.fullcontact.com/test")
            .termsService("https://terms.fullcontact.com/test")
            .collectionMethod("tag")
            .collectionLocation("US")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    permissionRequest.validate();

    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest().query(query).purposeId(1).channel("web").build();
    channelPurposeRequest.validate();
  }

  @Test
  public void nameWithLocationAsNullWithQueryableTest() throws FullContactException {
    MultifieldRequest query =
        FullContact.buildMultifieldRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .email("test@fullcontact.com")
            .build();
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(query)
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(1)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .enabled(true)
                    .ttl(365)
                    .build())
            .policyUrl("https://policy.fullcontact.com/test")
            .termsService("https://terms.fullcontact.com/test")
            .collectionMethod("tag")
            .collectionLocation("US")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    permissionRequest.validate();

    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest().query(query).purposeId(1).channel("web").build();
    channelPurposeRequest.validate();
  }

  @Test
  public void locationWithNameAsNullWithQueryableTest() throws FullContactException {
    MultifieldRequest query =
        FullContact.buildMultifieldRequest()
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
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(query)
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(1)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .enabled(true)
                    .ttl(365)
                    .build())
            .policyUrl("https://policy.fullcontact.com/test")
            .termsService("https://terms.fullcontact.com/test")
            .collectionMethod("tag")
            .collectionLocation("US")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    permissionRequest.validate();

    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest().query(query).purposeId(1).channel("web").build();
    channelPurposeRequest.validate();
  }

  @Test
  public void locationWithNoAddressLine1WithQueryableTest() throws FullContactException {
    MultifieldRequest query =
        FullContact.buildMultifieldRequest()
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
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(query)
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(1)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .enabled(true)
                    .ttl(365)
                    .build())
            .policyUrl("https://policy.fullcontact.com/test")
            .termsService("https://terms.fullcontact.com/test")
            .collectionMethod("tag")
            .collectionLocation("US")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    permissionRequest.validate();

    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest().query(query).purposeId(1).channel("web").build();
    channelPurposeRequest.validate();
  }

  @Test
  public void locationWithOnlyAddressLine1WithQueryableTest() throws FullContactException {
    MultifieldRequest query =
        FullContact.buildMultifieldRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(Location.builder().addressLine1("123/23").build())
            .profile(Profile.builder().url("http://linkedin/test").build())
            .build();
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(query)
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(1)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .enabled(true)
                    .ttl(365)
                    .build())
            .policyUrl("https://policy.fullcontact.com/test")
            .termsService("https://terms.fullcontact.com/test")
            .collectionMethod("tag")
            .collectionLocation("US")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    permissionRequest.validate();

    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest().query(query).purposeId(1).channel("web").build();
    channelPurposeRequest.validate();
  }

  @Test
  public void locationWithAddressLine1AndCityWithQueryableTest() throws FullContactException {
    MultifieldRequest query =
        FullContact.buildMultifieldRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(Location.builder().addressLine1("123/23").city("Denver").build())
            .personId("test")
            .build();
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(query)
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(1)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .enabled(true)
                    .ttl(365)
                    .build())
            .policyUrl("https://policy.fullcontact.com/test")
            .termsService("https://terms.fullcontact.com/test")
            .collectionMethod("tag")
            .collectionLocation("US")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    permissionRequest.validate();

    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest().query(query).purposeId(1).channel("web").build();
    channelPurposeRequest.validate();
  }

  @Test
  public void locationWithAddressLine1AndRegionWithQueryableTest() throws FullContactException {
    MultifieldRequest query =
        FullContact.buildMultifieldRequest()
            .name(PersonName.builder().full("Marian C Reed").build())
            .location(Location.builder().addressLine1("123/23").region("Denver").build())
            .maid("1234-sfnos-3432-sdjnwoi")
            .build();
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(query)
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(1)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .enabled(true)
                    .ttl(365)
                    .build())
            .policyUrl("https://policy.fullcontact.com/test")
            .termsService("https://terms.fullcontact.com/test")
            .collectionMethod("tag")
            .collectionLocation("US")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    permissionRequest.validate();

    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest().query(query).purposeId(1).channel("web").build();
    channelPurposeRequest.validate();
  }

  // Standard tests for MultiField query -- End

  // PermissionRequest validation specific tests - Start

  @Test
  public void nullQueryPermissionRequestTest() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage("Query is required for PermissionRequest");
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(1)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .enabled(true)
                    .ttl(365)
                    .build())
            .policyUrl("https://policy.fullcontact.com/test")
            .termsService("https://terms.fullcontact.com/test")
            .collectionMethod("tag")
            .collectionLocation("US")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    permissionRequest.validate();
  }

  @Test
  public void emptyConsentPurposePermissionRequestTest() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage("At least 1 `consentPurpose` is Required for PermissionRequest");
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(FullContact.buildMultifieldRequest().email("test@fullcontact.com").build())
            .policyUrl("https://policy.fullcontact.com/test")
            .termsService("https://terms.fullcontact.com/test")
            .collectionMethod("tag")
            .collectionLocation("US")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    permissionRequest.validate();
  }

  @Test
  public void invalidConsentPurposePermissionRequestTest1() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage("purposeId cannot be null for consentPurposes");
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(FullContact.buildMultifieldRequest().email("test@fullcontact.com").build())
            .consentPurpose(
                PurposeRequest.builder()
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .ttl(365)
                    .enabled(false)
                    .build())
            .policyUrl("https://policy.fullcontact.com/test")
            .termsService("https://terms.fullcontact.com/test")
            .collectionMethod("tag")
            .collectionLocation("US")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    permissionRequest.validate();
  }

  @Test
  public void invalidConsentPurposePermissionRequestTest2() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage("enabled cannot be null for consentPurposes");
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(FullContact.buildMultifieldRequest().email("test@fullcontact.com").build())
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(1)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .ttl(365)
                    .build())
            .policyUrl("https://policy.fullcontact.com/test")
            .termsService("https://terms.fullcontact.com/test")
            .collectionMethod("tag")
            .collectionLocation("US")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    permissionRequest.validate();
  }

  @Test
  public void invalidConsentPurposePermissionRequestTest3() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage("enabled cannot be null for consentPurposes");
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(FullContact.buildMultifieldRequest().email("test@fullcontact.com").build())
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(1)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .ttl(365)
                    .enabled(true)
                    .build())
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(3)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .ttl(365)
                    .build())
            .policyUrl("https://policy.fullcontact.com/test")
            .termsService("https://terms.fullcontact.com/test")
            .collectionMethod("tag")
            .collectionLocation("US")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    permissionRequest.validate();
  }

  @Test
  public void nullPolicyPermissionRequestTest() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage("policyUrl is required for PermissionRequest");
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(FullContact.buildMultifieldRequest().email("test@fullcontact.com").build())
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(1)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .enabled(true)
                    .ttl(365)
                    .build())
            .termsService("https://terms.fullcontact.com/test")
            .collectionMethod("tag")
            .collectionLocation("US")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    permissionRequest.validate();
  }

  @Test
  public void nullTermsPermissionRequestTest() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage("termsService is required for PermissionRequest");
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(FullContact.buildMultifieldRequest().email("test@fullcontact.com").build())
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(1)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .enabled(true)
                    .ttl(365)
                    .build())
            .policyUrl("https://policy.fullcontact.com/test")
            .collectionMethod("tag")
            .collectionLocation("US")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    permissionRequest.validate();
  }

  @Test
  public void nullCollectionMethodPermissionRequestTest() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage("collectionMethod is required for PermissionRequest");
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(FullContact.buildMultifieldRequest().email("test@fullcontact.com").build())
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(1)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .enabled(true)
                    .ttl(365)
                    .build())
            .policyUrl("https://policy.fullcontact.com/test")
            .termsService("https://terms.fullcontact.com/test")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    permissionRequest.validate();
  }

  @Test
  public void nullCollectionLocationPermissionRequestTest() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage("collectionLocation is required for PermissionRequest");
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(FullContact.buildMultifieldRequest().email("test@fullcontact.com").build())
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(1)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .enabled(true)
                    .ttl(365)
                    .build())
            .policyUrl("https://policy.fullcontact.com/test")
            .termsService("https://terms.fullcontact.com/test")
            .collectionMethod("tag")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    permissionRequest.validate();
  }

  @Test
  public void nullIPAddressPermissionRequestTest() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage("ipAddress is required for PermissionRequest");
    PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(FullContact.buildMultifieldRequest().email("test@fullcontact.com").build())
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(1)
                    .channel(List.of("web", "phone", "mobile", "offline", "email"))
                    .enabled(true)
                    .ttl(365)
                    .build())
            .policyUrl("https://policy.fullcontact.com/test")
            .termsService("https://terms.fullcontact.com/test")
            .collectionMethod("tag")
            .collectionLocation("US")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();
    permissionRequest.validate();
  }

  // PermissionRequest validation specific tests - End

  // ChannelPurposeRequest validation specific tests - Start

  @Test
  public void nullQueryChannelPurposeRequestTest() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage("Query is required for ChannelPurposeRequest");
    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest().purposeId(1).channel("web").build();
    channelPurposeRequest.validate();
  }

  @Test
  public void nullChannelInChannelPurposeRequestTest() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage("channel cannot be null for ChannelPurposeRequest");
    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest()
            .query(FullContact.buildMultifieldRequest().email("test@fullcontact.com").build())
            .purposeId(1)
            .build();
    channelPurposeRequest.validate();
  }

  @Test
  public void nullPurposeIdInChannelPurposeRequestTest() throws FullContactException {
    exceptionRule.expect(FullContactException.class);
    exceptionRule.expectMessage("purposeId cannot be null for ChannelPurposeRequest");
    ChannelPurposeRequest channelPurposeRequest =
        FullContact.buildChannelPurposeRequest()
            .query(FullContact.buildMultifieldRequest().email("test@fullcontact.com").build())
            .channel("web")
            .build();
    channelPurposeRequest.validate();
  }

  // ChannelPurposeRequest validation specific tests - End
}
