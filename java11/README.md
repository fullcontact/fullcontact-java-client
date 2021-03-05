# Java11 Client
API Clients for FullContact on V3 APIs supports Java11+

[![Maven Central](https://img.shields.io/maven-central/v/com.fullcontact.client/java11)](https://mvnrepository.com/artifact/com.fullcontact.client/java11)

This client provides an interface to interact with Enrich,
Resolve, Permission, Tags, Audience and Verification APIs. FullContact API Documentation is available
at: https://platform.fullcontact.com/docs

## Table of contents

   - [Add to your Project](#add-to-your-project)
   - [Dependencies](#dependencies)
   - [Providing Authentication](#providing-authentication-to-fullcontact-client)
   - [Building FullContact Client](#building-a-fullcontact-client)
        - [Retry Handler](#retryhandler)
   - [MultiFieldRequest](#multifieldrequest)
   - [Enrich](#enrich)
        - [Building a PersonRequest](#building-a-person-enrichresolve-request)
        - [Person Enrich Request and Response](#person-enrich-request-and-response)
        - [Company Enrich](#company-enrich-request-and-response)
            - [Lookup By Domain](#lookup-by-company-domain)
            - [Search By Company Name](#search-by-company-name)
   - [Resolve](#resolve)
        - [Resolve Request](#resolve-request)
        - [Resolve Response](#resolve-response)
   - [Tags/Metadata](#tagsmetadata)
        - [Tags Create](#creating-tags)
        - [Tags Get](#get-tags)
        - [Tags Delete](#delete-tags)
   - [Audience](#audience)
        - [Audience Create](#audience-create)
        - [Audience Download](#audience-download)
   - [Verification](#verification)
   - [Permission](#permission)
        - [Permission Create](#permission-create)
            - [PermissionRequest](#permissionrequest)
        - [Permission Delete](#permission-delete)
        - [Permission Find](#permission-find)
        - [Permission Current](#permission-current)
        - [Permission Verify](#permission-verify)
            - [ChannelPurposeRequest](#channelpurposerequest)
   - [Changelog](#changelog)
   
## Add to your Project

### Requirements

- Java 11 or later

### Gradle users

Add this dependency to your project's build file:

```groovy
implementation 'com.fullcontact.client:java11:3.0.0'
```

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
  <groupId>com.fullcontact.client</groupId>
  <artifactId>java11</artifactId>
  <version>3.0.0</version>
</dependency>
```

## Dependencies

__[Gson](https://github.com/google/gson)__ v2.8.6: Java library to convert Java Objects to JSON
and vice-versa.

## Providing Authentication to FullContact Client
FullContact client uses ```CredentialsProvider``` interface for Authentication. Different ways 
to provide authentication:

- __Static API Key provider__: 
```java
CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("your-key");
```
- __Through System Environment Variable__:
```java
CredentialsProvider envCredentialsProvider = new DefaultCredentialProvider("ENV_VAR");
```
- If __no__ ```CredentialsProvider``` is specified while building FullContact Client,
it automatically looks for API key from Environment variable ```"FC_API_KEY"```

(Don't have an API key? You can pick one up for free [right here.](https://www.fullcontact.com/developer-portal/))

## Building a FullContact Client
Our Java 11 Client library uses in-built HTTP Client.
Build your fcClient with:

| Build Parameters | Description | Default value | isOptional |
| ---------------- | ----------- | ------------- | ---------- |
| ```CredentialsProvider```| Used for Authentication | API Key through Environment variable```"FC_API_KEY"``` | No | 
| `Headers` | Any Custom Headers you want to add with every request, can include `Reporting-Key` as well. | No additional header | Yes |
| `connectTimeoutMillis` | Connection timeout for request | 3000ms | Yes |
| `retryHandler` | RetryHandler object | `DefaultRetryHandler` | Yes |

 
__Please note that you don't have to provide `Authorization` and `Content-Type` in the 
custom Headers map as these will be automatically added using ```CredentialsProvider``` provided.__ 
Custom headers provided will remain same and will be sent with every request made with this client. 
If you wish to change the headers, build a new client and provide new custom headers while building.

#### RetryHandler
In case of failure, FullContact Client will auto-retry for same request based on certain conditions set in RetryHandler
- Although optional, a custom Retry handler can be created by implementing `RetryHandler` interface and then used to build FC client. 
By default, client will use `DefaultRetryHandler` to schedule a retry for same request, with `retryAttempts = 1`, 
`retryDelayMillis = 1000`, and in case of `429`(rate limit error) or `503`(capacity limit error).

- This Client will auto-retry for a maximum of 5 times, even if higher value 
is set in the custom Retry Handler.

- Different `retryHandler` can be specified at each request level, 
By Default it will just use the `retryHandler` from client level.

__Note: Don't forget to call close() for every client created, 
at the end of application to avoid memory leak.__
```java
FullContact fcClient = FullContact.builder()
                .headers(customHeader)
                .credentialsProvider(staticCredentialsProvider)
                .connectTimeoutMillis(5000)
                .retryHandler(new CustomRetryHandler())
                .build();
```

## MultiFieldRequest
Ability to match on one or many input fields. The more contact data inputs you can provide, the better. 
By providing more contact inputs, the more accurate and precise we can get with our identity resolution capabilities.

- `email`: _String_
- `emails`: _List&lt;String&gt;_
- `phone`: _String_
- `phones`: _List&lt;String&gt;_
- `location`: _Location Object_
    - `addressLine1`: _String_
    - `addressLine2`: _String_
    - `city`: _String_
    - `region`: _String_
    - `regionCode`: _String_
    - `postalCode`: _String_
- `name`: _PersonName Object_
    - `full`: _String_
    - `given`: _String_
    - `family`: _String_
- `profiles`: _List&lt;Profile&gt;_
    - `service`: _String_
    - `username`: _String_
    - `userid`: _String_
    - `url`: _String_
- `maids`: _List<String>_
- `li_nonid`: _String_
- `recordId`: _String_
- `personId`: _String_
- `partnerId`: _String_

## Enrich
[Enrich API Reference](https://platform.fullcontact.com/docs/apis/enrich/introduction)
- `person.enrich`
- `company.enrich`
- `company.search`
#### Building a Person Enrich/Resolve Request
Our V3 Person Enrich supports __Multi Field Request:__ ability to match on __one or many__ input fields

You can build a Person Request by getting a builder from the fcClient or FullContact class
and setting different input parameters that you have. If you want to use Webhook, you can specify
it in `webhookUrl` field.
API can lookup and enrich individuals by sending any identifiers you may already have, 
as such specified in [MultiFieldRequest](#multifieldrequest). Some additional fields available in `PersonRequest`:

- `dataFilters`: _List&lt;String&gt;_
- `confidence`: _Confidence Enum_
- `infer`: _boolean_
- `webhookUrl`: _String_

```java
PersonRequest personRequest = fcClient
                            .buildPersonRequest()
                            .email("bart@fullcontact.com").email("bart.lorang@fullcontact.com")
                            .phone("+17202227799").phone("+13035551234")
                            .confidence(Confidence.HIGH)
                            .name(PersonName.builder().full("Bart Lorang").build())
                            .location(Location.builder().addressLine1("123 Main Street").addressLine2("Unit 2")
                                    .city("Denver").region("Colorado").build())
                            .profile(Profile.builder().service("twitter").userName("bartlorang").build())
                            .profile(Profile.builder().service("linkedin").url("https://www.linkedin.com/in/bartlorang").build())
                            .webhookUrl("")
                            .recordId("customer123")
                            .personId("eYxWc0B-dKRxerTw_uQpxCssM_GyPaLErj0Eu3y2FrU6py1J")
                            .li_nonid("CmQui5eT6tqBVqQ874WGCv4DNO_taXJOAxVlQ")       
                            .build();
```
#### Person Enrich Request and Response
You can send a request by calling ```enrich``` on fcClient and passing ```personRequest``` 
as a argument. It sends a Asynchronous request and a ```CompletableFuture<PersonResponse>``` 
is returned as response. You can then chain callbacks to this object as per your requirement.
There is a flag ```isSuccessful``` on Person Response object to check 
if the request was successful or not. If the request was unsuccessful, you can check the status code 
and message to determine the cause. 
```java
CompletableFuture<PersonResponse> personResponseCompletableFuture = fcClient.enrich(personRequest);
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
```

#### Company Enrich Request and Response
To Enrich Company data FullContact library provides two methods __Lookup by Company Domain__ or
__Search by Company Name__. More data is available through the Lookup by Company Domain, 
but if the domain is unknown, use our Search by Company Name to find the list of domains 
that could be related to the Company you are looking for and then call the Lookup by 
Company Domain with that domain to get the full information about the company.

##### Lookup by Company Domain
- Request:
```java
CompanyRequest companyRequest = fcClient.buildCompanyRequest().domain("fullcontact.com").build();
```
- Response:
```java
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
```

##### Search by Company Name
- Request:
    - Parameters:
        - `companyName`
        - `webhookUrl` 
        - `location`
        - `locality` 
        - `region`
        - `country`
```java
CompanyRequest companySearch = fcClient.buildCompanyRequest().companyName("fullContact").build();
```
- Response: It returns a CompletableFuture of ```CompanySearchResponseList```, from which you can 
get a ```List``` for CompanySearch responses.
```java
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
```

## Resolve
[Resolve API Reference](https://platform.fullcontact.com/docs/apis/resolve/introduction)
- `identity.map`
- `identity.resolve`
- `identity.delete`
#### Resolve Request
Resolve uses `ResolveRequest` object for its request which supports
 __Multi Field Request:__ ability to match on __one or many__ input fields

You can build a Resolve Request by getting a builder from the fcClient or FullContact class
and setting different input parameters that you have.

Note: For `identity.map` any of `email`, `phone`, `profile`, `name & location` 
must be present.
 
 
API can lookup and resolve individuals by sending any identifiers you may already have, 
as such specified in [MultiFieldRequest](#multifieldrequest). Additional field available in `ResolveRequest`:

- `tags`: _List&lt;Tag&gt;_


```java
ResolveRequest resolveRequest = fcClient
                            .buildResolveRequest()
                            .email("bart@fullcontact.com").email("bart.lorang@fullcontact.com")
                            .phone("+17202227799").phone("+13035551234")
                            .name(PersonName.builder().full("Bart Lorang").build())
                            .location(Location.builder().addressLine1("123 Main Street").addressLine2("Unit 2")
                                    .city("Denver").region("Colorado").build())
                            .profile(Profile.builder().service("twitter").userName("bartlorang").build())
                            .profile(Profile.builder().service("linkedin").url("https://www.linkedin.com/in/bartlorang").build())
                            .recordId("customer123")
                            .li_nonid("CmQui5eT6tqBVqQ874WGCv4DNO_taXJOAxVlQ")      
                            .build();
```

#### Resolve Response
All resolve methods returns a `CompletableFuture<ResolveResponse>`

```java
CompletableFuture<ResolveResponse> mapResponse = fcClient.identityMap(resolveRequest);

mapResponse.thenAccept(
          response -> {
            System.out.println("identity.map " + response.toString());
          });

CompletableFuture<ResolveResponse> resolveResponse = fcClient.identityResolve(resolveRequest);

resolveResponse.thenAccept(
          response -> {
            System.out.println("identity.resolve " + response.toString());
          });

CompletableFuture<ResolveResponse> deleteResponse = fcClient.identityDelete(resolveRequest);

deleteResponse.thenAccept(
          response -> {
            System.out.println("identity.delete " + response.toString());
          });
```

### Tags/Metadata

[Tags API Reference](https://platform.fullcontact.com/docs/apis/resolve/customer-tags)
- `tags.create`
- `tags.get`
- `tags.delete`

FullContact provides the ability to store customer tags/metadata to each record within a customer's Private Identity 
Cloud for continuous updates, retrievals and deletes across both 1st party as well as 2nd party data partnerships.

#### Creating Tags
Tags can be added while mapping records using `identity.map` API or later using `tags.create` API. 
Once a Customer Record ID has been mapped, customer tags can continue to be added to the originally provided Record ID

##### Tags Request
- Request Parameters:
    - `recordId`: _String_
    - `tags`: _List<Tag>_
        - `key`: _String_
        - `value`: _String_
       
```java
// Building Request
TagsRequest tagsRequest =
          FullContact.buildTagsRequest()
              .recordId("k2")
              .tag(Tag.builder().key("gender").value("something").build())
              .build();
// Sending Request
CompletableFuture<TagsResponse> tagsResponseCompletableFuture =
          fcClient.tagsCreate(tagsRequest);

tagsResponseCompletableFuture.thenAccept(
          tagsResponse -> {
            System.out.println(tagsResponse.getRecordId());
            System.out.println(tagsResponse.getPartnerId());
            System.out.println(tagsResponse.getTags());
          });
```

#### Get Tags
This will return all customer tags that are associated to a mapped record using `recordId`.

```java
CompletableFuture<TagsResponse> tagsResponseCompletableFuture = fcClient.tagsGet("recordId");

tagsResponseCompletableFuture.thenAccept(
          tagsResponse -> {
            System.out.println(tagsResponse.getRecordId());
            System.out.println(tagsResponse.getPartnerId());
            System.out.println(tagsResponse.getTags());
          });
```

#### Delete Tags
This will remove specific or all customer tags that are attached to a mapped record.

```java
// Building Request
TagsRequest tagsRequest =
          FullContact.buildTagsRequest()
              .recordId("k2")
              .tag(Tag.builder().key("gender").value("something").build())
              .build();
// Sending Request
CompletableFuture<TagsResponse> tagsResponseCompletableFuture =
          fcClient.tagsDelete(tagsRequest);

tagsResponseCompletableFuture.thenAccept(
          tagsResponse -> {
            System.out.println(tagsResponse.statusCode);
          });
```

### Audience
- `audience.create`
- `audience.download`

This endpoint can be used in order to obtain multiple individuals based upon the key, value 
tag inputs (both are required as input) in order to suppress or take action upon certain audiences 
for data onboarding or audience analysis.

#### Audience Create
The Audience Creation endpoint requires a at least one `Tag` and valid `webhookURL` to be present in order to 
send a message when the audience creation is complete and ready to be downloaded.

```java
AudienceRequest audienceRequest =
          FullContact.buildAudienceRequest()
              .tag(Tag.builder().key("gender").value("male").build())
              .webhookUrl("your-webhook-url")
              .build();

 CompletableFuture<AudienceResponse> audienceResponseCompletableFuture =
          fcClient.audienceCreate(audienceRequest);

 audienceResponseCompletableFuture.thenAccept(
          audienceResponse -> {
            System.out.println(audienceResponse.isSuccessful);
            System.out.println(audienceResponse.getRequestId());
            });
```

#### Audience Download
When `audience.create` result is ready, `requestId` from its response can be used to download the audience data.
A utility method is provided `getFileFromBytes(String fileName)` which generates a file in `json.gz` format
with audience data bytes.
```java
CompletableFuture<AudienceResponse> audienceResponseCompletableFuture =
          fcClient.audienceDownload(requestId);

audienceResponseCompletableFuture.thenAccept(
          audienceResponse -> {
            try {
              audienceResponse.getFileFromBytes(requestId);
            } catch (IOException e) {
              e.printStackTrace();
            }
          });
```

## Verification
[EmailVerification API Reference](https://platform.fullcontact.com/docs/apis/verification/introduction)
- `v2/verification/email`

FullContact Email Verification API accepts single `email` request, as a `String`. Requests are sent 
using HTTP GET and request email is set as a query parameter. It returns a `CompletableFuture<EmailVerificationResponse>`
```java
CompletableFuture<EmailVerificationResponse> emailVerificationResponse = fcClient.emailVerification("bart@fullcontact.com");
```

## Permission
[Permission APIs Reference](https://platform.fullcontact.com/docs/apis/permission/introduction)

- `permission.create`
- `permission.delete`
- `permission.find`
- `permission.current`
- `permission.verify`

FullContact's Permission API is a ledger that allows brands, partners, agencies alike to track a variety of 
permission and preference operations over time paired with details about what an individual's consent entails, 
such as:

- Acknowledgment of consented data
- Purpose of consented data
- Which Partner or Vendor of FullContact's is enabling the consent on the user
- Location of Terms of service and/or Privacy Policy

Each preference acknowledgment is securely captured, maintained and accurately linked using [MultiFieldRequest](#multifieldrequest) 
to a FullContact PersonID. This allows for real-time consent updates, suppression, creation and retrieval.

### Permission Create
Create a new permission for a given consumer record. A successful creation request will return `FCResponse` with a 202 Response.

Permission Create takes `PermissionRequest` and following fields are required to create a permission:

- One or many of the acceptable multi field inputs
- Permission Purposes purpose ID, enabled (true/false) & channel (valid channel of email, mobile, web, phone and/or offline)
- Collection Method
- Collection Location
- Policy URL
- Terms of Service

#### PermissionRequest
- `query` : [MultiFieldRequest](#multifieldrequest)
- `consentPurposes` : [PurposeRequest](#purposerequest)
- `locale` : _String_
- `ipAddress` : _String_
- `language` : _String_
- `collectionMethod` : _String_
- `collectionLocation` : _String_
- `policyUrl` : _String_
- `termsService` : _String_
- `tcf` : _String_
- `timestamp` : _Long_

#### PurposeRequest
- `purposeId` : _Integer_
- `channel` : _List&lt;String&gt;_
- `ttl` : _Integer_
- `enabled` : _Boolean_

```java
MultifieldRequest mFQuery =
        FullContact.buildMultifieldRequest()
            .email("bart@fullcontact.com")
            .build()

PermissionRequest permissionRequest =
        FullContact.buildPermissionRequest()
            .query(mFQuery)
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(1)
                    .channel(Arrays.asList("web", "phone", "mobile", "offline", "email"))
                    .enabled(true)
                    .ttl(365)
                    .build())
            .consentPurpose(
                PurposeRequest.builder()
                    .purposeId(4)
                    .channel(Arrays.asList("web", "phone", "mobile", "offline", "email"))
                    .enabled(true)
                    .ttl(365)
                    .build())
            .policyUrl("https://policy.fullcontact.com/test")
            .termsService("https://terms.fullcontact.com/test")
            .collectionMethod("tag")
            .collectionLocation("US")
            .ipAddress("127.0.0.1")
            .language("en")
            .locale("US")
            .timestamp(16528103039L)
            .build();

FCResponse response = fcClient.permissionCreate(permissionRequest).get();
Assert.assertTrue(response.isSuccessful());
Assert.assertEquals(202, response.getStatusCode());
```

### Permission Delete
Delete a previously permitted consumer record by providing one or many of the acceptable multi field inputs.
A successful deletion request will result in a 202 Response.

Permission Create takes [PermissionRequest](#permissionrequest) as input parameter
and returns a Response of type `FCResponse`
```java
FCResponse response = fcClient.permissionDelete(permissionRequest).get();
Assert.assertTrue(response.isSuccessful());
Assert.assertEquals(202, response.getStatusCode());
```

### Permission Find
Find and retrieve the permission history for an individual using one or many of the acceptable multi field inputs. 
A successful response will return all permissions history for an individual.

Permission Find takes [MultiFieldRequest](#multifieldrequest) as input parameter 
and returns a Response of type `PermissionResponseList`

```java
MultifieldRequest query = FullContact.buildMultifieldRequest().email("test@fullcontact.com").build();
PermissionResponseList response = fcClient.permissionFind(query).get();

Assert.assertTrue(response.isSuccessful());
Assert.assertEquals(200, response.getStatusCode());
Assert.assertEquals("create", response.permissionResponseList.get(0).getPermissionType());
```

### Permission Current
Retrieve an individual's current permissions state by purpose by providing one or many of the acceptable multi field inputs.
Permission Current takes [MultiFieldRequest](#multifieldrequest) as input parameter 
and returns a Response of type `PermissionCurrentResponseMap`

```java
MultifieldRequest query = FullContact.buildMultifieldRequest().email("test@fullcontact.com").build();
PermissionCurrentResponseMap response = fcClient.permissionCurrent(query).get();

Assert.assertTrue(response.isSuccessful());
Assert.assertEquals(200, response.getStatusCode());
Assert.assertTrue(response.getResponseMap().get(3).get("mobile").isEnabled());
```

### Permission Verify
Retrieve and verify the permission state for an individual by purpose and channel. 
Response will return the current purposes and whether or not an individual has provided permissions.

The following fields are required to verify a permission:
- One or many of the acceptable multi field inputs
- Permission Purposes purpose ID & channel (valid channel of email, mobile, web, phone and/or offline)

Permission Current takes [ChannelPurposeRequest](#channelpurposerequest) as input parameter 
and returns a Response of type `ConsentPurposeResponse`

#### ChannelPurposeRequest
- `query` : [MultiFieldRequest](#multifieldrequest)
- `purposeId` : _Integer_
- `channel` : _List&lt;String&gt;_

```java
MultifieldRequest query = FullContact.buildMultifieldRequest().email("test@fullcontact.com").build();
ChannelPurposeRequest channelPurposeRequest = FullContact
                                              .buildChannelPurposeRequest()
                                              .query(query).channel("web").purposeId(6)
                                              .build();

ConsentPurposeResponse response = fcClient.permissionVerify(channelPurposeRequest).get();

Assert.assertTrue(response.isSuccessful());
Assert.assertEquals(200, response.getStatusCode());
Assert.assertEquals("OK", response.getMessage());
Assert.assertEquals(6, response.getPurposeId());
Assert.assertTrue(response.isEnabled());
```

## Changelog
- v3.0.0 - Support for Permission APIs
- v2.3.0 - Separated MultifieldReq and less strict on reqeust validation
- v2.2.1 - Updated docs links, and added lombok config
- v2.2.0 - Support for Tag and Audience APIs
- v2.1.1 - Removing Shadow jar, transitive dependency fix
- v2.0.1 - Added Email Verification API
- v2.0.0 - Added Support for Resolve APIs
- v1.0.0 - Initial Release, Support for Person and Company Enrich
- If you are updating the version of this client from `1.0.0`, please note that
 `retryAttmpts` and `retryDelayMillis` fields were removed from FullContact client
 for a [RetryHandler](https://github.com/fullcontact/fullcontact-java-client/tree/master/java11#retryhandler)
