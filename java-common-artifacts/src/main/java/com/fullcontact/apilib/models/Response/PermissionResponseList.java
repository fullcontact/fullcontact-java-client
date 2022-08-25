package com.fullcontact.apilib.models.Response;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class PermissionResponseList extends FCResponse {
  public List<PermissionResponse> permissionResponseList;
}
