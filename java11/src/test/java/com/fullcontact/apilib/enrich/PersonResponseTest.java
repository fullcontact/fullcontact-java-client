package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.models.Response.PersonResponse;
import org.junit.Assert;
import org.junit.Test;

public class PersonResponseTest {

  @Test
  public void personResponseModelDeserializationTest() {
    PersonResponse response =
        (PersonResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_001"),
                PersonResponse.class);
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(200, response.getStatusCode());
    Assert.assertEquals("OK", response.getMessage());
    Assert.assertEquals("Marquita H Ross", response.getFullName());
    Assert.assertEquals("37-47", response.getAgeRange());
    Assert.assertEquals("Female", response.getGender());
    Assert.assertEquals("San Francisco, California, United States", response.getLocation());
    Assert.assertEquals("Senior Petroleum Manager", response.getTitle());
    Assert.assertEquals("Mostow Co.", response.getOrganization());
    Assert.assertEquals("Senior Petroleum Manager at Mostow Co.", response.getBio());
    Assert.assertEquals(
        "https://img.fullcontact.com/sandbox/1gagrO2K67_oc5DLG_siVCpYVE5UvCu2Z.png",
        response.getAvatar());
    Assert.assertEquals("http://marquitaas8.com/", response.getWebsite());
    Assert.assertEquals("https://twitter.com/marqross91", response.getTwitter());
    Assert.assertEquals(
        "https://www.linkedin.com/in/marquita-ross-5b6b72192", response.getLinkedin());
    Assert.assertEquals("Marquita", response.getDetails().get().getName().getGiven());
    Assert.assertEquals("Ross", response.getDetails().get().getName().getFamily());
    Assert.assertEquals("Marquita H Ross", response.getDetails().get().getName().getFull());
    Assert.assertEquals("35-44", response.getDetails().get().getAge().getRange());
    Assert.assertEquals(42, (int) response.getDetails().get().getAge().getValue());
    Assert.assertEquals("Female", response.getDetails().get().getGender());
    Assert.assertEquals(2, response.getFamilyInfo().get().getTotalAdults());
    Assert.assertEquals(1, response.getFamilyInfo().get().getTotalChildren());
    Assert.assertEquals(3, response.getFamilyInfo().get().getTotalPeopleInHousehold());
    Assert.assertEquals(
        "Multi Family Dwelling/Apartment", response.getHomeInfo().get().getDwellingType());
    Assert.assertEquals(513, response.getHomeInfo().get().getHomeValueEstimate());
    Assert.assertEquals(4, response.getHomeInfo().get().getLoanToValueEstimate());
    Assert.assertTrue(response.getLocationInfo().get().isSeasonalAddress());
    Assert.assertEquals("PO Box", response.getLocationInfo().get().getCarrierRoute());
    Assert.assertEquals("807", response.getLocationInfo().get().getDesignatedMarketArea());
    Assert.assertEquals(
        "41860 - San Francisco-Oakland-Hayward, CA Metropolitan Statistical Area",
        response.getLocationInfo().get().getCoreBasedStatisticalArea());
    Assert.assertEquals("AT_LARGE", response.getLocationInfo().get().getCongressionalDistrict());
    Assert.assertEquals(222, response.getLocationInfo().get().getNumericCountyCode());
    Assert.assertEquals(
        "PRESENT", response.getHouseHoldPresence().get().getMultigenerationalResident());
    Assert.assertEquals("PRESENT", response.getHouseHoldPresence().get().getChildren());
    Assert.assertEquals(
        45,
        response.getDetails().get().getHousehold().getFinance().getDiscretionaryIncomeEstimate());
    Assert.assertEquals(
        "$150,000 - $199,999",
        response.getDetails().get().getHousehold().getFinance().getHouseholdIncomeEstimate());
    Assert.assertEquals(
        "$25,000 - $49,999",
        response
            .getDetails()
            .get()
            .getHousehold()
            .getFinance()
            .getCashValueBalanceHouseholdEstimate());
    Assert.assertEquals("Probable Homeowner", response.getDemographics().get().getLivingStatus());
    Assert.assertEquals(
        "Professional - Engineer/Industrial", response.getDemographics().get().getOccupation());
    Assert.assertEquals("Multiple Bank Card", response.getFinance().get().getBankCard());
    Assert.assertEquals("Multiple Retail Card", response.getFinance().get().getRetailCard());
    Assert.assertTrue(response.getFinance().get().isActiveLineOfCredit());
    Assert.assertEquals(145, response.getDetails().get().getCensus().getBasicTractNumber());
    Assert.assertEquals(2, response.getDetails().get().getCensus().getBasicBlockGroup());
    Assert.assertEquals(
        "High School Diploma",
        response.getDetails().get().getCensus().getYear2010().getEducationLevel());
    Assert.assertEquals(
        90, response.getDetails().get().getCensus().getYear2010().getPercent().getHomeowner());
    Assert.assertEquals(
        35, response.getDetails().get().getCensus().getYear2010().getMedian().getHomeValue());
    Assert.assertEquals(
        17,
        response
            .getDetails()
            .get()
            .getCensus()
            .getYear2010()
            .getPopulationDensity()
            .getCentileInUs());
    Assert.assertEquals(
        29, response.getDetails().get().getCensus().getYear2010().getSocioEconomicScore());
    Assert.assertTrue(response.getBuyerCatalogPayment().get().isCreditCard());
    Assert.assertTrue(response.getBuyerCatalogPayment().get().isHouseCharge());
    Assert.assertTrue(response.getBuyerCatalogPayment().get().isMasterCard());
    Assert.assertTrue(response.getBuyerCatalogApparel().get().isChildren());
    Assert.assertTrue(response.getBuyerCatalogApparel().get().isMen());
    Assert.assertTrue(response.getDetails().get().getBuyer().getCatalog().isBeauty());
    Assert.assertTrue(response.getDetails().get().getBuyer().getCatalog().isVideoEntertainment());
    Assert.assertTrue(response.getDetails().get().getBuyer().getRetail().isBeauty());
    Assert.assertTrue(response.getBuyerRetailApparel().get().isChildren());
    Assert.assertTrue(response.getDetails().get().getSurvey().getOwn().isOwnDigitalCamera());
    Assert.assertTrue(response.getDetails().get().getSurvey().getCollectibles().isCoins());
    Assert.assertTrue(
        response.getDetails().get().getSurvey().getCreditCards().getPremium().isAmex());
    Assert.assertTrue(response.getDetails().get().getSurvey().getCreditCards().isDebit());
    Assert.assertTrue(response.getDetails().get().getSurvey().getDietConcerns().isHealthy());
    Assert.assertTrue(response.getDetails().get().getSurvey().getHobby().isCigarSmoking());
    Assert.assertTrue(
        response.getDetails().get().getSurvey().getHobby().getGardening().isFlowers());
    Assert.assertTrue(response.getDetails().get().getSurvey().getHobby().isSpirituality());
    Assert.assertTrue(response.getDetails().get().getSurvey().isInvestments());
    Assert.assertTrue(response.getDetails().get().getSurvey().getMusic().isClassical());
    Assert.assertTrue(response.getDetails().get().getSurvey().getReading().isHistory());
    Assert.assertTrue(response.getDetails().get().getSurvey().getSporting().isFitness());
    Assert.assertTrue(response.getDetails().get().getSurvey().getTravel().isCasinoVacations());
    Assert.assertTrue(response.getDetails().get().getSurvey().getElectronics().isHomeTheater());
    Assert.assertTrue(response.getDetails().get().getSurvey().getPurchase().isUsesCoupons());
    Assert.assertEquals(
        "marqross91", response.getDetails().get().getProfiles().getTwitter().getUsername());
    Assert.assertEquals(
        "Senior Petroleum Manager at Mostow Co.", response.getTwitterObject().get().getBio());
    Assert.assertEquals(
        "marquita-ross-5b6b72192", response.getLinkedinObject().get().getUsername());
    Assert.assertEquals(
        "http://www.pinterest.com/marquitaross006/",
        response.getDetails().get().getProfiles().getPinterest().getUrl());
    Assert.assertEquals(
        "California", response.getDetails().get().getLocations().get(0).getRegion());
    Assert.assertEquals("Mostow Co.", response.getDetails().get().getEmployment().get(0).getName());
    Assert.assertEquals(
        "https://img.fullcontact.com/sandbox/1gagrO2K67_oc5DLG_siVCpYVE5UvCu2Z.png",
        response.getDetails().get().getPhotos().get(0).getValue());
    Assert.assertEquals(
        "University of California, Berkeley",
        response.getDetails().get().getEducation().get(0).getName());
    Assert.assertEquals(
        "http://marquitaas8.com/", response.getDetails().get().getUrls().get(0).getValue());
    Assert.assertEquals("9", response.getEpsilon().get("epsilon_key_1"));
    Assert.assertEquals("Q", response.getEpsilon().get("epsilon_key_3"));
    Assert.assertEquals("11", response.getEpsilon().get("epsilon_key_5"));
  }

  @Test
  public void responseStatus400Test() {
    PersonResponse personResponse =
        (PersonResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_002"),
                PersonResponse.class);
    Assert.assertFalse(personResponse.isSuccessful());
    Assert.assertEquals(400, personResponse.getStatusCode());
    Assert.assertEquals("Unable to process JSON", personResponse.getMessage());
  }

  @Test
  public void responseStatus202Test() {
    PersonResponse response =
        (PersonResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_003"),
                PersonResponse.class);
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(202, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("Queued for search"));
  }

  @Test
  public void responseStatus401Test() {
    PersonResponse response =
        (PersonResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_004"),
                PersonResponse.class);
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(401, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("401: Invalid access token"));
  }

  @Test
  public void responseStatus404Test() {
    PersonResponse response =
        (PersonResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_005"),
                PersonResponse.class);
    Assert.assertTrue(response.isSuccessful());
    Assert.assertEquals(404, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("Profile not found"));
  }

  @Test
  public void responseStatus403Test() {
    PersonResponse response =
        (PersonResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_006"),
                PersonResponse.class);
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(403, response.getStatusCode());
    Assert.assertTrue(response.getMessage().contains("API Key is missing or invalid."));
  }

  @Test
  public void responseStatus422Test() {
    PersonResponse response =
        (PersonResponse)
            FullContact.getFCResponse(
                HttpResponseTestObjects.httpResponseTestObjectProvider("tc_007"),
                PersonResponse.class);
    Assert.assertFalse(response.isSuccessful());
    Assert.assertEquals(422, response.getStatusCode());
    Assert.assertTrue(
        response
            .getMessage()
            .contains("Input domain parameter (\"fullcontact\") does not contain a valid domain."));
  }
}
