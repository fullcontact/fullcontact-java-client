package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
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
