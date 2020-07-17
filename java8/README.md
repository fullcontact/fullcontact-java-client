# Java8 Client
API Clients for FullContact on V3 APIs supports Java8+

[![Maven Central](https://img.shields.io/maven-central/v/com.fullcontact.client/java8)](https://mvnrepository.com/artifact/com.fullcontact.client/java8)

## Add to your Project

### Requirements

- Java 1.8 or later

### Gradle users

Add this dependency to your project's build file:

```groovy
implementation 'com.fullcontact.client:java8:2.1.0'
```

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
  <groupId>com.fullcontact.client</groupId>
  <artifactId>java8</artifactId>
  <version>2.1.0</version>
</dependency>
```

## Dependencies

__[Retrofit2](https://github.com/square/retrofit/)__ v2.7.0 : HTTP Client used for Java 8

__[Retrofit2 Gson Converter](https://github.com/square/retrofit/tree/master/retrofit-converters/gson)__ v2.7.0: 
Converter which uses [Gson](https://github.com/google/gson) for serialization to and from JSON

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
## Enrich
[Enrich API Reference](https://dashboard.fullcontact.com/api-ref#enrich)
- `person.enrich`
- `company.enrich`
- `company.search`
#### Building a Person Enrich/Resolve Request
Our V3 Person Enrich supports __Multi Field Request:__ ability to match on __one or many__ input fields

You can build a Person Request by getting a builder from the fcClient or FullContact class
and setting different input parameters that you have. If you want to use Webhook, you can specify
it in `webhookUrl` field.
API can lookup and enrich individuals by sending any identifiers you may already have, 
such as: 

- `email`: _String_
- `emails`: _List<String>_
- `phone`: _String_
- `phones`: _List<String>_
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
- `profiles`: _List<Profile>_
    - `service`: _String_
    - `username`: _String_
    - `userid`: _String_
    - `url`: _String_
- `dataFilters`: _List<String>_
- `maids`: _List<String>_
- `confidence`: _Confidence Enum_
- `infer`: _boolean_
- `webhookUrl`: _String_
- `recordId`: _String_
- `personId`: _String_

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
[Resolve API Reference](https://dashboard.fullcontact.com/api-ref#resolve-2)
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
such as: 

- `email`: _String_
- `emails`: _List<String>_
- `phone`: _String_
- `phones`: _List<String>_
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
- `profiles`: _List<Profile>_
    - `service`: _String_
    - `username`: _String_
    - `userid`: _String_
    - `url`: _String_
- `maids`: _List<String>_
- `recordId`: _String_
- `personId`: _String_

```java
ResolveRequest personRequest = fcClient
                            .buildResolveRequest()
                            .email("bart@fullcontact.com").email("bart.lorang@fullcontact.com")
                            .phone("+17202227799").phone("+13035551234")
                            .name(PersonName.builder().full("Bart Lorang").build())
                            .location(Location.builder().addressLine1("123 Main Street").addressLine2("Unit 2")
                                    .city("Denver").region("Colorado").build())
                            .profile(Profile.builder().service("twitter").userName("bartlorang").build())
                            .profile(Profile.builder().service("linkedin").url("https://www.linkedin.com/in/bartlorang").build())
                            .recordId("customer123")     
                            .build();
```

#### Resolve Response
All resolve methods returns a `CompletableFuture<ResolveResponse>`

```java
CompletableFuture<ResolveResponse> mapResponse = fcClient.identityMap(personRequest);
      mapResponse.thenAccept(
          response -> {
            System.out.println("identity.map " + response.toString());
          });

CompletableFuture<ResolveResponse> resolveResponse = fcClient.identityResolve(personRequest);
resolveResponse.thenAccept(
          response -> {
            System.out.println("identity.resolve " + response.toString());
          });

CompletableFuture<ResolveResponse> deleteResponse = fcClient.identityDelete(personRequest);
      deleteResponse.thenAccept(
          response -> {
            System.out.println("identity.delete " + response.toString());
          });
```

## Verification
[EmailVerification API Reference](https://dashboard.fullcontact.com/api-ref#verification)
- `v2/verification/email`

FullContact Email Verification API accepts single `email` request, as a `String`. Requests are sent 
using HTTP GET and request email is set as a query parameter. It returns a `CompletableFuture<EmailVerificationResponse>`
```java
CompletableFuture<EmailVerificationResponse> emailVerificationResponse = fcClient.emailVerification("bart@fullcontact.com");
```

## Changelog
- If you are updating the version of this client from `1.0.0`, please note that
 `retryAttmpts` and `retryDelayMillis` fields were removed from FullContact client
 for a [RetryHandler](https://github.com/fullcontact/fullcontact-java-client/tree/master/java8#retryhandler)