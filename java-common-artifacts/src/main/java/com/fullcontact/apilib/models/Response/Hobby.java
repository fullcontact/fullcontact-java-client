package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class Hobby {
  private String baking;
  private String cigarSmoking;
  private String cooking;
  private String crafts;
  private String gardening;
  private String homeStudyCourses;
  private String quilting;
  private String selfImprovementCourses;
  private String woodworking;
  private String photography;
  private String careerAdvancementCourses;

  private String any;
  private String automotiveWork;
  private String birdFeedingOrWatching;
  private String culturalArtsOrEvents;
  private String gourmetFoods;
  private String homeImprovementOrDIY;
  private String motorcycleRiding;
  private String scrapbooking;
  private String sewingOrNeedleworkOrKnitting;
  private String wine;
}
