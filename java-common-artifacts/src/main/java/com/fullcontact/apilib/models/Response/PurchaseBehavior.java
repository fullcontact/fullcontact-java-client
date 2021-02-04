package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class PurchaseBehavior {
  private Apparel apparel;
  private Payment payment;
  private boolean artsAntiques,
      automative,
      beauty,
      books,
      childrenProducts,
      collectibles,
      homeOffice,
      crafts,
      electronics,
      foodBeverages,
      furniture,
      garden,
      generalMerchandise,
      gift,
      health,
      holiday,
      homeCare,
      homeFurnishings,
      housewares,
      jewelry,
      linens,
      music,
      novelty,
      otherMerchServices,
      personalCare,
      pets,
      photoVideoEquipment,
      specialtyFood,
      specialtyGifts,
      sportsLeisure,
      stationery,
      travel,
      videoEntertainment,
      continuityShopper,
      onlineShopper;
}
