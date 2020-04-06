package com.fullcontact.app;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.auth.CredentialsProvider;
import com.fullcontact.apilib.auth.StaticApiKeyCredentialProvider;
import com.fullcontact.apilib.enrich.FullContact;
import com.fullcontact.apilib.models.Location;
import com.fullcontact.apilib.models.PersonName;
import com.fullcontact.apilib.models.Profile;
import com.fullcontact.apilib.models.Request.CompanyRequest;
import com.fullcontact.apilib.models.Request.PersonRequest;
import com.fullcontact.apilib.models.Response.CompanyResponse;
import com.fullcontact.apilib.models.Response.CompanySearchResponseList;
import com.fullcontact.apilib.models.Response.PersonResponse;
import com.fullcontact.apilib.models.enums.Confidence;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class App {

  public static void main(String[] args) {
    try {
      HashMap<String, String> customHeader = new HashMap<>();
      customHeader.put("Reporting-Key", "clientXYZ");
      CredentialsProvider staticCredentialsProvider =
          new StaticApiKeyCredentialProvider("your-key-here");

      // Building FullContact client
      FullContact fcClient =
          FullContact.builder()
              .headers(customHeader)
              .credentialsProvider(staticCredentialsProvider)
              .connectTimeoutMillis(3000)
              .retryAttempts(3)
              .retryDelayMillis(1000)
              .build();

      // Person Enrich: Request Build
      PersonRequest personRequest =
          fcClient
              .buildPersonRequest()
              .email("bart@fullcontact.com")
              .phone("+17202227799")
              .phone("+13035551234")
              .confidence(Confidence.HIGH)
              .name(PersonName.builder().full("Bart Lorang").build())
              .location(
                  Location.builder()
                      .addressLine1("123 Main Street")
                      .addressLine2("Unit 2")
                      .city("Denver")
                      .region("Colorado")
                      .build())
              .profile(Profile.builder().service("twitter").username("bartlorang").build())
              .profile(
                  Profile.builder()
                      .service("linkedin")
                      .url("https://www.linkedin.com/in/bartlorang")
                      .build())
              .webhookUrl("")
              .build();

      // Person Enrich: sending Asynchronous Request
      CompletableFuture<PersonResponse> personResponseCompletableFuture =
          fcClient.enrich(personRequest);
      personResponseCompletableFuture.thenAccept(
          personResponse -> {
            System.out.println(
                "Person Response "
                    + personResponse.isSuccessful()
                    + " "
                    + personResponse.getStatusCode()
                    + " "
                    + personResponse.getMessage());
          });

      // Company Enrich by Domain: Request Build
      CompanyRequest companyRequest =
          fcClient.buildCompanyRequest().domain("fullcontact.com").build();

      // Company Enrich by Domain: sending Asynchronous Request
      CompletableFuture<CompanyResponse> companyResponseCompletableFuture =
          fcClient.enrich(companyRequest);
      companyResponseCompletableFuture.thenAccept(
          companyResponse -> {
            System.out.println(
                "Company Response "
                    + companyResponse.isSuccessful()
                    + " "
                    + companyResponse.getLinkedin()
                    + " "
                    + companyResponse.getStatusCode()
                    + " "
                    + companyResponse.getMessage());
          });

      // Company Search by Name: Request build
      CompanyRequest companySearch =
          fcClient.buildCompanyRequest().companyName("fullContact").build();

      // Company Search by Name: sending Asynchronous Request
      CompletableFuture<CompanySearchResponseList> companySearchResponseListCompletableFuture =
          fcClient.search(companySearch);
      companySearchResponseListCompletableFuture.thenAccept(
          companySearchResponseList -> {
            System.out.println(
                "Company search "
                    + companySearchResponseList.isSuccessful()
                    + " "
                    + companySearchResponseList.getMessage()
                    + " "
                    + companySearchResponseList.getStatus()
                    + " "
                    + companySearchResponseList
                        .getCompanySearchResponses()
                        .get(0)
                        .getLookupDomain());
          });
      Thread.sleep(5000);
      fcClient.close();

    } catch (FullContactException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
