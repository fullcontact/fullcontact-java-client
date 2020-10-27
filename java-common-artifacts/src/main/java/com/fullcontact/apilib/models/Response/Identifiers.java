package com.fullcontact.apilib.models.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Identifiers {
  private List<Maids> maids;
  private List<String> personIds, recordIds, li_nonid, partnerIds;
}
