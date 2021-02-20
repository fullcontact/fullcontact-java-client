package com.fullcontact.apilib.models.Request;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.models.enums.Confidence;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

/** Class to create request for Person Enrich */
@Getter
@SuperBuilder(builderMethodName = "personRequestBuilder")
public class PersonRequest extends MultifieldRequest {
  private String webhookUrl;
  private Confidence confidence;
  private boolean infer;
  private List<String> dataFilter;

  protected PersonRequest(PersonRequestBuilder<?, ?> b) {
    super(b);
    this.infer = true;
    this.webhookUrl = b.webhookUrl;
    this.confidence = b.confidence;
    this.infer = b.infer;
    this.dataFilter = b.dataFilter;
  }
}
