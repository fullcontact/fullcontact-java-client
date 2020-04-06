package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.models.Request.CompanyRequest;
import com.fullcontact.apilib.models.enums.Sort;
import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ComapanyRequestBuildTest {
  private static final Gson gson = new Gson();

  @Test
  public void companyEnrichRequestBuildAndSerializeTest() throws IOException {
    CompanyRequest companyRequest =
        FullContact.buildCompanyRequest().domain("fullcontact.com").build();
    try (BufferedReader br =
        new BufferedReader(
            new FileReader("src/test/resources/companyEnrichRequestBuildAndSerializeTest.txt"))) {
      String line;
      StringBuilder sb = new StringBuilder();
      while ((line = br.readLine()) != null) {
        sb.append(line.trim());
      }
      Assert.assertEquals(sb.toString(), gson.toJson(companyRequest));
    }
  }

  @Test
  public void companySearchRequestBuildAndSerializeTest() throws IOException {
    CompanyRequest companyRequest =
        FullContact.buildCompanyRequest()
            .companyName("Full Contact")
            .locality("Denver")
            .region("Colorado")
            .webhookUrl("http://www.fullcontact.com/hook")
            .country("US")
            .location("Denver, CO")
            .sort(Sort.employees)
            .build();
    try (BufferedReader br =
        new BufferedReader(
            new FileReader("src/test/resources/companySearchRequestBuildAndSerializeTest.txt"))) {
      String line;
      StringBuilder sb = new StringBuilder();
      while ((line = br.readLine()) != null) {
        sb.append(line.trim());
      }
      Assert.assertEquals(sb.toString(), gson.toJson(companyRequest));
    }
  }
}
