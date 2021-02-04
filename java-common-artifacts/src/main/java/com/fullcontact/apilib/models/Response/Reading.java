package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class Reading {
  private boolean likesToRead,
      astrology,
      bibleOrDevotional,
      bestSellingFiction,
      audiobooks,
      childrens,
      cooking,
      computer,
      countryLifestyle,
      fashion,
      history,
      interiorDecorating,
      health,
      military,
      mystery,
      naturalHealthRemedies,
      entertainment,
      romance,
      scienceFiction,
      technology,
      sports,
      worldNewsOrPolitics,
      suspense,
      bestSellers,
      bookClub,
      comics,
      financial,
      homeAndGarden,
      selfImprovement,
      travel,
      magazines;
}
