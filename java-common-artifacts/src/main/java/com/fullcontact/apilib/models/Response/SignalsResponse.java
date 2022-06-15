package com.fullcontact.apilib.models.Response;

import com.fullcontact.apilib.models.PersonName;
import lombok.*;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class SignalsResponse extends FCResponse {
  private List<EmailAndPhone> emails, phones;
  private List<String> personIds;
  private List<Maids> maids;
  private PersonName name;
  private List<PanoId> panoIds;
  private List<NonId> nonIds;
  private List<IpAddress> ipAddresses;
  private SocialProfile socialProfiles;
  // TODO: Add demogrphics, employment and maybe modify socialProfiles
}
