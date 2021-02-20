package com.fullcontact.apilib.models.Response;

import lombok.*;

import java.util.Map;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class EmailVerificationResponse extends FCResponse {
  private int status;
  private String requestId;
  private String[] unknownEmails, failedEmails;
  private Map<String, EmailProperties> emails;
}
