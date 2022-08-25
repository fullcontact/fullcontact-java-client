package com.fullcontact.apilib.models.Response;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class Identifiers {
  private List<Maids> maids;
  private List<String> personIds, recordIds, li_nonid, partnerIds;
}
