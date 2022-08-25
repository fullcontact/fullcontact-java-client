package com.fullcontact.apilib.models.Request;

import com.fullcontact.apilib.models.enums.Confidence;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/** Class to create request for Person Enrich */
@Getter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(builderMethodName = "personRequestBuilder", toBuilder = true)
public class PersonRequest extends MultifieldRequest {
  private String webhookUrl;
  private Confidence confidence;
  @Builder.Default private boolean infer = true;
  private List<String> dataFilter;
  /** Limit the number of MAIDs in response */
  private Integer maxMaids;
}
