package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class Survey {
  private Own own;
  private Collectibles collectibles;
  private DietConcerns dietConcerns;
  private Hobby hobby;
  private Investments investments;
  private Music music;
  private Reading reading;
  private Sporting sporting;
  private Travel travel;
  private Purchase purchase;
  private Donor donor;
  private MailOrder mailOrder;
  private Other other;
  private Social social;
}

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
class Investments {
  private String juvenileLifeInsurance;
  private String burialInsurance;
  private String insurance;
  private String investments;
  private String lifeInsurance;
  private String medicareCoverage;
  private String mutualFunds;
  private String stocksOrBonds;
}

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
class Donor {
  private String activeMilitary;
  private String animalWelfare;
  private String artsOrCultural;
  private String cancer;
  private String catholic;
  private String childrens;
  private String charitable;
  private String humanitarian;
  private String nativeAmerican;
  private String otherReligious;
  private String politicalConservative;
  private String politicalLiberal;
  private String veteran;
  private String wildlifeEnvironmental;
  private String worldRelief;
}

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
class MailOrder {
  private String any;
  private String apparel;
  private String books;
  private String buyer;
  private String childrensProducts;
  private String food;
  private String gifts;
  private String healthOrBeautyProducts;
  private String homeFurnishing;
  private String jewelry;
  private String magazines;
  private String videosOrDVD;
  private String womensPlusApparel;
}

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
class Other {
  private String electronics;
  private String grandchildren;
  private String militaryVeteran;
  private String onlineHousehold;
  private String scienceAndNewTechnology;
  private String swimmingPool;
}

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
class Social {
  private String facebookUser;
  private String instagramUser;
  private String pinterestUser;
  private String twitterUser;
}
