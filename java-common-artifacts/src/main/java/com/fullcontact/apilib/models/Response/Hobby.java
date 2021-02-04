package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class Hobby {
  private Gardening gardening;
  private boolean general,
      baking,
      birdWatching,
      cars,
      cigarSmoking,
      gourmetCooking,
      cooking,
      crafts,
      casinoGambling,
      homeImprovement,
      homeStudyCourses,
      knitting,
      lotteries,
      quilting,
      selfImprovementCourses,
      sewing,
      theater,
      woodworking,
      wineAppreciation,
      photography,
      exercise3xPerWeek,
      scrapBooking,
      lowFatCooking,
      careerAdvancementCourses,
      jewelryMaking,
      diy,
      green,
      socialNetworking,
      spirituality;
}
