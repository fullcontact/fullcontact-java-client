package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class Purchase {
  private String artsCraftsRecency;
  private String beautyAndSpaRecency;
  private String beveragesRecency;
  private String booksRecency;
  private String clubContinuity;
  private String gardenAndBackyardRecency;
  private String homeDecorRecency;
  private String sweepstakes;
  private String maleApparelRecency;
  private String musicVideosRecency;
  private String specialtyFoodsAndGiftsRecency;
  private String sportsAndOutdoorRecency;
  private String toolsAndElectronicsRecency;
  private String femaleAndMaleRecency;
  private String femaleBrandAndFitRecency;
}
