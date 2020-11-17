package com.fullcontact.apilib.models.Response;

import lombok.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AudienceResponse {
  private String requestId;
  private byte[] audienceBytes;
  public boolean isSuccessful;
  public int statusCode;
  public String message;

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
