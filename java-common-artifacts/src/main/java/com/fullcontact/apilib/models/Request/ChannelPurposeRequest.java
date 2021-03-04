package com.fullcontact.apilib.models.Request;

import com.fullcontact.apilib.FullContactException;
import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class ChannelPurposeRequest {
  private MultifieldRequest query;
  private Integer purposeId;
  private String channel;

  public void validate() throws FullContactException {
    if (this.getQuery() == null) {
      throw new FullContactException("Query is required for ChannelPurposeRequest");
    }
    this.getQuery().validate();
    if (this.getPurposeId() == null) {
      throw new FullContactException("purposeId cannot be null for ChannelPurposeRequest");
    }
    if (this.getChannel() == null) {
      throw new FullContactException("channel cannot be null for ChannelPurposeRequest");
    }
  }
}
