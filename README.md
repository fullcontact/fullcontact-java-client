## Java Client
The official [FullContact](https://www.fullcontact.com/) Java Client Library for the FullContact V3 APIs.

- [Java11+](https://github.com/fullcontact/fullcontact-java-client/tree/master/java11) client 
- [Java8+](https://github.com/fullcontact/fullcontact-java-client/tree/master/java8) client.


## Add to your Project

### Gradle users

Add this dependency to your project's build file:

- Java11+
```groovy
implementation 'com.fullcontact.client:java11:2.2.0'
```
- Java8+
```groovy
implementation 'com.fullcontact.client:java8:2.2.0'
```

### Maven users

Add this dependency to your project's POM:

- Java11+
```xml
<dependency>
  <groupId>com.fullcontact.client</groupId>
  <artifactId>java11</artifactId>
  <version>2.2.0</version>
</dependency>
```
- Java8+
```xml
<dependency>
  <groupId>com.fullcontact.client</groupId>
  <artifactId>java8</artifactId>
  <version>2.2.0</version>
</dependency>
```


## Working with FullContact Client
FullContact client supports v3 Enrich and Resolve APIs which are super simplified to easily 
enrich Person and Company data and Resolve fragmented customer data. 
All the API requests are over HTTPS using POST method 
with content sent as JSON. This library supports Multi Field Request, Person Enrichment 
& Data Packs, Company Enrichment & Data Packs and Webhooks. Just build a FullContact 
Client with your API Key, make a enrich request and get a response object back.

### Quick Overview
If you are not familiar with the FullContact APIs, complete details can be found 
@[API documentation](https://www.fullcontact.com/developer/docs/) 


FullContact Client provides an object layer to FullContact API communication, 
but understanding Enrich API, webhooks, request and response parameters, 
and common snags is still important.

Once you’re on board with the API behavior, FullContact Client library should simplify 
your integration.

### Supported APIs
- _[Enrich](https://dashboard.fullcontact.com/api-ref#enrich)_
    - `person.enrich`
    - `company.enrich`
    - `company.search`
- Private Identity Cloud
    - _[Resolve](https://dashboard.fullcontact.com/api-ref#resolve-2)_
        - `identity.map`
        - `identity.resolve`
        - `identity.delete`
    - [Tags](https://dashboard.fullcontact.com/api-ref#customer-tags)
        - `tags.create`
        - `tags.get`
        - `tags.delete`
    - [Audience](https://dashboard.fullcontact.com/api-ref#customer-tags)
        - `audience.create`
        - `audience.download`
- _[Verification](https://dashboard.fullcontact.com/api-ref#verification)_
    - `v2/verification/email`