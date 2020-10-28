package com.fullcontact.app;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.auth.CredentialsProvider;
import com.fullcontact.apilib.auth.StaticApiKeyCredentialProvider;
import com.fullcontact.apilib.enrich.FullContact;
import com.fullcontact.apilib.models.Location;
import com.fullcontact.apilib.models.PersonName;
import com.fullcontact.apilib.models.Profile;
import com.fullcontact.apilib.models.Tag;
import com.fullcontact.apilib.models.Request.*
import com.fullcontact.apilib.models.Response.*;
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
              .retryHandler(new CustomRetryHandler())
              .build();

      // Person Enrich: Request Build
      PersonRequest personRequest =
              FullContact
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
              .recordId("customer123")
              .personId("eYxWc0B-dKRxerTw_uQpxCssM_GyPaLErj0Eu3y2FrU6py1J")
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
                    + companySearchResponseList.getStatusCode()
                    + " "
                    + companySearchResponseList
                        .getCompanySearchResponses()
                        .get(0)
                        .getLookupDomain());
          });

      // Identity Map request
      ResolveRequest identityMapRequest =
          FullContact.buildResolveRequest()
              .email("bart@fullcontact.com")
              .recordId("customer123")
              .build();
      // Identity Map Response
      CompletableFuture<ResolveResponse> mapResponse = fcClient.identityMap(identityMapRequest);
      mapResponse.thenAccept(
          response -> {
            System.out.println("identity.map " + response.toString());
          });

      // Identity Map Request with Tags
        ResolveRequest mapRequestWithTags =
                FullContact.buildResolveRequest()
                        .email("bart@fullcontact.com")
                        .recordId("customer123")
                        .tag(Tag.builder().key("client").value("test").build())
                        .build();
      // Identity Map Response with Tags
        CompletableFuture<ResolveResponse> mapResponseWithTags =
                fcClient.identityMap(mapRequestWithTags);
        mapResponseWithTags.thenAccept(
                response -> {
                    System.out.println(response.getRecordIds());
                    System.out.println(response.getPersonIds());
                    System.out.println(response.getTags());
                });

      // Identity Resolve Request
      ResolveRequest identityResolveRequest =
          FullContact.buildResolveRequest().recordId("customer123").build();
      // Identity Resolve Response
      CompletableFuture<ResolveResponse> resolveResponse =
          fcClient.identityResolve(identityResolveRequest);
      resolveResponse.thenAccept(
          response -> {
            System.out.println("identity.resolve " + response.toString());
          });

       // Identity Resolve Response with Tags
        CompletableFuture<ResolveResponseWithTags> resolveResponseWithTags =
                fcClient.identityResolveWithTags(identityResolveRequest);
        resolveResponseWithTags.thenAccept(
                response -> {
                    System.out.println(response.getRecordIds());
                    System.out.println(response.getPersonIds());
                    System.out.println(response.getTags());
                });

      // Identity Delete Response
      CompletableFuture<ResolveResponse> deleteResponse =
          fcClient.identityDelete(identityResolveRequest);
      deleteResponse.thenAccept(
          response -> {
            System.out.println("identity.delete " + response.toString());
          });
      // Tags Create Request
        TagsRequest tagsCreateRequest =
                FullContact.buildTagsRequest()
                        .recordId("customer123")
                        .tag(Tag.builder().key("gender").value("male").build())
                        .build();
      // Tags Create Response
        CompletableFuture<TagsResponse> tagsCreateResponseCF = fcClient.tagsCreate(tagsCreateRequest);
        tagsCreateResponseCF.thenAccept(
                tagsResponse -> {
                    System.out.println(tagsResponse.getRecordId());
                    System.out.println(tagsResponse.getPartnerId());
                    System.out.println(tagsResponse.getTags());
                });

      // Tags Get Request
        CompletableFuture<TagsResponse> tagsGetResponseCF = fcClient.tagsGet("customer123");
        tagsGetResponseCF.thenAccept(
                tagsResponse -> {
                    System.out.println(tagsResponse.getRecordId());
                    System.out.println(tagsResponse.getPartnerId());
                    System.out.println(tagsResponse.getTags());
                });
      // Tags Delete Request
        TagsRequest tagsDeleteRequest =
                FullContact.buildTagsRequest()
                        .recordId("customer123")
                        .tag(Tag.builder().key("gender").value("male").build())
                        .build();
      // Tags Create Response
        CompletableFuture<TagsResponse> tagsDeleteResponseCF = fcClient.tagsDelete(tagsDeleteRequest);
        tagsDeleteResponseCF.thenAccept(
                tagsResponse -> {
                    System.out.println(tagsResponse.statusCode);
                });
      // Audience Create Request
        AudienceRequest audienceRequest =
                FullContact.buildAudienceRequest()
                        .tag(Tag.builder().key("gender").value("male").build())
                        .webhookUrl("your-webhook-url")
                        .build();
      // Audience Create Response
        CompletableFuture<AudienceResponse> audienceCreateResponseCF = fcClient.audienceCreate(audienceRequest);
        audienceCreateResponseCF.thenAccept(
                audienceResponse -> {
                    System.out.println(audienceResponse.getRequestId());
                });
      // Audience Download
        CompletableFuture<AudienceResponse> audienceDownloadResponseCF = fcClient.audienceDownload(requestId);
        audienceDownloadResponseCF.thenAccept(
                audienceResponse -> {
                    try {
                        audienceResponse.getFileFromBytes(requestId);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
      //Email Verification
      CompletableFuture<EmailVerificationResponse> emailVerificationResponse =
          fcClient.emailVerification("bart@fullcontact.com");
      emailVerificationResponse.thenAccept(
          response -> {
            System.out.println("send Safely " + response.getEmails().get("bart@fullcontact.com").isSendSafely())
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
