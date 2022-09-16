package com.fullcontact.apilib.models.Request;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.models.Tag;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;

@Builder(toBuilder = true)
@EqualsAndHashCode
@Getter
public class AudienceRequest {
  private String webhookUrl;
  @Singular private List<Tag> tags;

  public static class AudienceRequestBuilder {

    public AudienceRequest build() throws FullContactException {
      List<Tag> tags =
          this.tags != null ? Collections.unmodifiableList(this.tags) : Collections.emptyList();
      this.validate();
      return new AudienceRequest(webhookUrl, tags);
    }

    public void validate() throws FullContactException {
      if (this.webhookUrl != null && !this.webhookUrl.trim().isEmpty()) {

      } else {
        throw new FullContactException("WebhookUrl is mandatory for creating Audience");
      }
      if (this.tags != null && this.tags.size() > 0) {

      } else {
        throw new FullContactException("At least 1 Tag is mandatory for creating Audience");
      }
      for (Tag tag : this.tags) {
        if (!tag.isValid()) {
          throw new FullContactException("Both Key and Value must be populated for a Tag");
        }
      }
    }
  }
}
