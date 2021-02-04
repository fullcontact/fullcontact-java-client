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
  private CreditCards creditCards;
  private DietConcerns dietConcerns;
  private Hobby hobby;
  private Music music;
  private Reading reading;
  private Sporting sporting;
  private Travel travel;
  private boolean religious, grandchildren, onlinePurchaser, investments;
  private Electronics electronics;
  private Purchase purchase;
}
