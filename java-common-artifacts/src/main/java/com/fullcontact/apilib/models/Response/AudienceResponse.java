package com.fullcontact.apilib.models.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AudienceResponse extends FCResponse {
  private String requestId;
  private byte[] audienceBytes;

  public AudienceResponse(byte[] audienceBytes) {
    this.audienceBytes = audienceBytes;
  }

  public void getFileFromBytes(String fileName) throws IOException {
    if (!fileName.endsWith(".json.gz")) {
      fileName = fileName + ".json.gz";
    }
    File audienceFile = new File(fileName);
    FileOutputStream fos = new FileOutputStream(audienceFile);
    fos.write(this.audienceBytes);
    fos.close();
  }
}
