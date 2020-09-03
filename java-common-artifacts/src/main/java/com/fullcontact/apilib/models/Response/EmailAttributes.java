package com.fullcontact.apilib.models.Response;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class EmailAttributes {
  private boolean validSyntax, deliverable, catchall, risky, disposable;
}
