package com.fullcontact.apilib.models.Request;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.models.Tag;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;

@EqualsAndHashCode
@Builder(toBuilder = true)
@Getter
public class TagsRequest {
  private String recordId;
  @Singular private List<Tag> tags;

  public static class TagsRequestBuilder {
    public TagsRequest build() throws FullContactException {
      List<Tag> tags =
          this.tags != null ? Collections.unmodifiableList(this.tags) : Collections.emptyList();
      this.validate();
      return new TagsRequest(recordId, tags);
    }

    /**
     * Check if tags are valid or not
     *
     * @throws FullContactException if any tag is invalid
     */
    public void validate() throws FullContactException {
      if (this.recordId == null) {
        throw new FullContactException("RecordId must be present for creating Tags");
      }
      if (this.tags != null && this.tags.size() > 0) {

      } else {
        throw new FullContactException("Tags must be populated in Tags Create request");
      }
      for (Tag tag : this.tags) {
        if (!tag.isValid()) {
          throw new FullContactException("Both Key and Value must be populated for adding a Tag");
        }
      }
    }
  }
}
