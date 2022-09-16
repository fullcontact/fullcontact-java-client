package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class Sporting {
  private String campingOrHiking;
  private String fishing;
  private String golf;
  private String nascar;
  private String boatingOrSailing;
  private String cycling;
  private String fitnessExcercise;
  private String bigGameHunting;
  private String huntingOrShooting;
  private String sportsMerchandiseOrActivewearRecency;
  private String runningOrJogging;
  private String skiingOrSnowboarding;
  private String sportsParticipation;
  private String walkingForHealth;
  private String yogaOrPilates;
}
