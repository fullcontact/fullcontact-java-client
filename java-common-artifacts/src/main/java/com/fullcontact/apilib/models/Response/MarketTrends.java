package com.fullcontact.apilib.models.Response;

import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class MarketTrends {
  private Switchers switchers;
  private Seekers seekers;
  private Enthusiasts enthusiasts;
  private Attendees attendees;
  private Buyers buyers;
  private Chef chef;
  private Customers customers;
  private Consumers consumers;
  private Donor donor;
  private Owners owners;
  private Travellers travellers;
  private Readers readers;
  private Voters voters;
  private Subscribers subscribers;
  private Planners planners;
  private Purchasers purchasers;
  private Spenders spenders;
  private Shoppers shoppers;
  private Stores stores;
  private Users users;

  @Getter
  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  @NoArgsConstructor
  public static class Switchers {
    private String alcoholBeverage;
    private String beerBrand;
    private String breakfastMeatBrand;
    private String chocolateCandyBrand;
    private String cocaColaBrand;
    private String coldCerealBrand;
    private String energyDrink;
    private String frozenFoodBrand;
    private String householdCleaningProductsBrand;
    private String insurance;
    private String job;
    private String mobilePhoneService;
    private String naturalCheeseSlicesBrand;
    private String naturalShreddedCheeseBrand;
    private String nutritionalHealthBarBrand;
    private String refrigeratedLunchMeatBrand;
    private String snackBarGranolaBarBrand;
    private String softDrinksBrand;
    private String spiritsBrand;
    private String yogurtBrand;
  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  @NoArgsConstructor
  public static class Seekers {
    private String freshFood;
    private String homeCleaningNewProduct;
    private String laundryNewProduct;
    private String onlineDegreeEducation;
    private String personalCareNewProduct;
    private String premiumNaturalHomeCleaners;
    private String scent;
    private String unscentedProduct;
  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  @NoArgsConstructor
  public static class Enthusiasts {
    private String artHouseMovie;
    private String barandLoungeFood;
    private String baseball;
    private String basketball;
    private String brandLoyalists;
    private String breakfastDining;
    private String carryOut;
    private String casualDining;
    private String christianorGospelMusic;
    private String cigarPipe;
    private String coffee;
    private String convenienceHomeCleaners;
    private String countryMusic;
    private String dinnerDining;
    private String discountMovie;
    private String domesticBeer;
    private String extremeFitness;
    private String fantasySports;
    private String fineDining;
    private String football;
    private String freeStreaming;
    private String frequentMovie;
    private String gamingnonMobileDevices;
    private String hardCider;
    private String hardSeltzer;
    private String hockey;
    private String homeShoppingNetwork;
    private String importBeer;
    private String kroger;
    private String latinMusic;
    private String liquor;
    private String lunchDining;
    private String meditation;
    private String mobileGaming;
    private String openingWeekendMovie;
    private String paidStreaming;
    private String petsAreFamily;
    private String quickServiceRestaurant;
    private String redWine;
    private String soccer;
    private String target;
    private String valueChains;
    private String walmart;
    private String whiteWine;
    private String yogaPilates;
  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  @NoArgsConstructor
  public static class Attendees {
    private String amusementPark;
    private String culturalArtsEvents;
    private String liveMusicConcert;
    private String professionalSportsEvents;
  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  @NoArgsConstructor
  public static class Buyers {
    private String autoInsuranceSelfServeOnline;
    private String christmasOrnamentsCollectibles;
    private String heavyFiberFocusedFood;
    private String heavyGlutenFreeFood;
    private String heavyLowFatFood;
    private String onlineHomeCleaningProduct;
    private String onlineInsurance;
    private String onlineLaundryProduct;
    private String onlinePersonalCareProduct;
    private String onlinePetFood;
  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  @NoArgsConstructor
  public static class Chef {
    private String experimental;
    private String master;
    private String realIngredient;
  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  @NoArgsConstructor
  public static class Customers {
    private String aT_TCellPhoneCustomer;
    private String amazonPrime;
    private String annuity;
    private String autoInsurancePremiumDiscountviaTelematicsCustomer;
    private String cateringDelivery;
    private String cateringPickUp;
    private String certificatesofDeposit;
    private String clicktoCartHomeDelivery;
    private String clicktoCartPickUp;
    private String communityBankCustomer;
    private String convenience;
    private String deposit;
    private String directMediaPreference;
    private String financialAdvisor;
    private String frequentATM;
    private String groceryLoyaltyCard;
    private String interestCheckingPreference;
    private String internationalWirelessorLandline;
    private String internetResearchPreference;
    private String lending;
    private String loyalFinancialInstitution;
    private String mensBigandTallApparel;
    private String nationalBankCustomer;
    private String newRoof;
    private String onlineDeliveryRestaurant;
    private String onlinePickUpRestaurant;
    private String plantoPurchaseSmartHomeProducts;
    private String qSRCash;
    private String quantumUpgrade;
    private String regionalBankCustomer;
    private String restaurantLoyaltyCard;
    private String selfInsuredDental;
    private String smartHome;
    private String socialMediaPreference;
    private String sprintCellPhoneCustomer;
    private String studentLoan;
    private String subscriptionorAutoShipmentClothAccessories;
    private String subscriptionorAutoShipmentFoodorBeverage;
    private String subscriptionorAutoShipmentHouseholdProduct;
    private String subscriptionorAutoShipmentPersonalCare;
    private String subscriptionorAutoShipmentPetProducts;
    private String subscriptionorAutoShipment;
    private String tMobileCellPhoneCustomer;
    private String vOIPLandline;
    private String verizonCellPhoneCustomer;
    private String wiredService;
    private String womensPlusSizeApparel;
  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  @NoArgsConstructor
  public static class Consumers {
    private String airlineUpgraders;
    private String autoInsurance;
    private String autoInsuranceAgentSold;
    private String autoInsuranceCallCenterSold;
    private String cableBundlecableinternethomephone;
    private String casinoGamer;
    private String coinsCollector;
    private String conservativeInvestmentStyle;
    private String contactlessPayApplication;
    private String cordCutters;
    private String creditCardAttritionHouseholds;
    private String creditCardRevolvers;
    private String creditUnionMember;
    private String dietConsciousHouseholds;
    private String doitYourselfer;
    private String employerProvidedHealthInsurancePolicyHolders;
    private String frequentOnlineMovieViewers;
    private String freshFoodDelivery;
    private String futureInvestors;
    private String gamers;
    private String gigEconomyEmployees;
    private String groceryStoreFrequenters;
    private String homeEntertainers;
    private String homeRemodelers;
    private String hotelLoyaltyProgramMembers;
    private String intheMarkettoGetaHomeLoan;
    private String intheMarkettoPurchaseaHome;
    private String intendtoPurchaseaSamsungmobiledevice;
    private String intendtopurchase5GService;
    private String investmentTrustBankingPreference;
    private String likelyCruiser;
    private String likelyMortgageRefinancers;
    private String likelyPlannedGivers;
    private String likelytoSufferfromInsomnia;
    private String likelytoUseanInvestmentBroker;
    private String likelytohaveaMortgage;
    private String longRoadTripTakers;
    private String longTermCare;
    private String lowInterestCreditCard;
    private String lowSodium;
    private String mealCombo;
    private String mealKitDelivery;
    private String medicaidPotentialQualifiedHousehold;
    private String medicareDualEligibleHousehold;
    private String medicarePlanDPrescriptionDrugHealth;
    private String mobileBrowsers;
    private String movieLoyaltyProgramMembers;
    private String naturalGreenProductHomeCleaners;
    private String non401kMutualFundInvestors;
    private String non401kStocksBondsInvestors;
    private String pandemicInRestaurantDiners;
    private String pandemicLuxurySpenders;
    private String pandemicRiskTolerant;
    private String paychecktoPaycheck;
    private String personalTraveler;
    private String plantoPurchaseHomeSecuritySystems;
    private String plantogetFitnessMembership;
    private String rentersandAutoInsuranceJointPolicyHolders;
    private String retailTexters;
    private String retiredbutStillWorking;
    private String satelliteBundlesatelliteinethomeorwireless;
    private String selfPayHealthInsurance;
    private String seniorCaregivers;
    private String seniorLivingSearchers;
    private String sociallyInfluenced;
    private String technologyEarlyAdopters;
    private String teleMedicine;
    private String termLife;
    private String underbanked;
    private String uninsuredforHealth;
    private String upcomingRetirees65andOlder;
    private String vegetarians;
    private String vehicleDIYrs;
    private String weeklyOnlineBankers;
    private String wellnessHouseholdsHealth;
    private String wholeLife;
    private String wiredLineVideoConnectors;
    private String workforSmallCompanyOfferingHealthInsurance;
  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  @NoArgsConstructor
  public static class Donor {
    private String animalWelfare;
    private String cancer;
    private String childrensCauses;
    private String consistentReligious;
    private String environmental;
    private String highDollarOtherCausesnonReligious;
    private String highDollarReligiousCauses;
    private String liberalCauses;
    private String university;
    private String veteran;
  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  @NoArgsConstructor
  public static class Owners {
    private String _401k;
    private String androidSmartPhone;
    private String appleSmartPhone;
    private String boat;
    private String educationSavingsPlan;
    private String multiPolicyInsurance;
    private String pet;
    private String prepaidCard;
    private String secondHome;
    private String smartTV;
    private String tablet;
    private String timeshare;
    private String veterinarianInfluencedPet;
  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  @NoArgsConstructor
  public static class Planners {
    private String budgetMeal;
    private String meal;
    private String preShop;
  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  @NoArgsConstructor
  public static class Purchasers {
    private String aCAHealthInsurance;
    private String autoLoan;
    private String brandDrivenHomeCleaners;
    private String frequentMobile;
    private String frequentOnlineMusic;
    private String greenProduct;
    private String homeWarranty;
    private String impulse;
    private String medicareAdvantagePlan;
    private String midmarketTermLifeInsurance;
    private String midmarketWholeLifeInsurance;
    private String newLuxuryVehicle;
    private String newnonLuxuryVehicle;
    private String organicFood;
    private String organicProduct;
    private String petInsurance;
    private String rV;
    private String vehicle;
    private String webSurferBrickMortar;
    private String webandBrickMortarViewerOnline;
  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  @NoArgsConstructor
  public static class Readers {
    private String avidBook;
    private String bibleDevotional;
    private String book;
    private String entertainment;
    private String label;
    private String retailerCircular;
    private String romance;
    private String sports;
  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  @NoArgsConstructor
  public static class Shoppers {
    private String bargainHotel;
    private String bargain;
    private String everydayLowPrice;
    private String financialInstitution;
    private String highend;
    private String multiRetailer;
    private String oneStop;
    private String priceMatchers;
    private String privateLabel;
    private String quickShopAtWalmartOrTarget;
    private String stockUp;
    private String whatsOnSale;
  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  @NoArgsConstructor
  public static class Spenders {
    private String pandemicDecreasedSpenders;
    private String vacationSpenders;
  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  @NoArgsConstructor
  public static class Stores {
    private String stockUpAtGroceryStores;
    private String stockUpAtWalmart;
  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  @NoArgsConstructor
  public static class Subscribers {
    private String cableTVPremium;
    private String financialHealthNewsletter;
    private String onlineMagazineNewspaper;
    private String retailerEmail;
    private String satelliteRadio;
  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  @NoArgsConstructor
  public static class Travellers {
    private String business;
    private String international;
    private String rVTripTakers;
  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  @NoArgsConstructor
  public static class Users {
    private String app;
    private String brandMotivatedLaundry;
    private String brandMotivatedPersonalCareProduct;
    private String brandedRetailCreditCard;
    private String convenienceDrivenPersonalCareProduct;
    private String creditCardBalanceTransfer;
    private String debitCardRewards;
    private String debitCard;
    private String groceryStoreApp;
    private String heavyCoupon;
    private String mobileBanking;
    private String mobileShoppingList;
    private String naturalProductPersonalCareProduct;
    private String onlineBroker;
    private String onlineSavings;
    private String paperShoppingList;
    private String premiumNaturalPersonalCareProduct;
    private String priceMotivatedLaundryProduct;
    private String priceMotivatedPersonalCareProduct;
    private String primaryCellPhone;
    private String publicTransportation;
    private String restaurantApp;
    private String restaurantLoyaltyApp;
    private String rewardsCardCashBack;
    private String rewardsCardOther;
    private String smartPhone;
    private String sociallyActiveonFacebook;
    private String sociallyActiveonFacebookBrandLikers;
    private String sociallyActiveonFacebookCategoryRecommenders;
    private String sociallyActiveonPinterest;
    private String sociallyActiveonTwitter;
    private String solarRoofingInterest;
    private String targetCartwheel;
    private String uberOrLyft;
    private String vehicleServiceCenter;
    private String walmartSavingCatcher;
  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  @NoArgsConstructor
  public static class Voters {
    private String democratic;
    private String independent;
    private String likely;
    private String republican;
    private String swing;
  }
}
