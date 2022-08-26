## Java Client
The official [FullContact](https://www.fullcontact.com/) Java Client Library for the FullContact V3 APIs.

- [Java11+](https://github.com/fullcontact/fullcontact-java-client/tree/master/java11) client 
- [Java8+](https://github.com/fullcontact/fullcontact-java-client/tree/master/java8) client.


## Add to your Project

### Gradle users

Add this dependency to your project's build file:

- Java11+
```groovy
implementation 'com.fullcontact.client:java11:4.2.0'
```
- Java8+
```groovy
implementation 'com.fullcontact.client:java8:4.2.0'
```

### Maven users

Add this dependency to your project's POM:

- Java11+
```xml
<dependency>
  <groupId>com.fullcontact.client</groupId>
  <artifactId>java11</artifactId>
  <version>4.2.0</version>
</dependency>
```
- Java8+
```xml
<dependency>
  <groupId>com.fullcontact.client</groupId>
  <artifactId>java8</artifactId>
  <version>4.2.0</version>
</dependency>
```


## Working with FullContact Client
FullContact client supports v3 Enrich and Resolve, Verify and Permission APIs,
and are super simplified to easily 
enrich Person and Company data, Resolve fragmented customer data and Verify signals. 
All the API requests are over HTTPS using POST method 
with content sent as JSON. This library supports Multi Field Request, Person Enrichment 
& Data Packs, Company Enrichment & Data Packs and Webhooks, Resolve & PIC, Verify and Permission. 
Just build a FullContact 
Client with your API Key, make a http request and get a response object back.

### Quick Overview
If you are not familiar with the FullContact APIs, complete details can be found 
@[API documentation](https://docs.fullcontact.com/docs/getting-started) 


FullContact Client provides an object layer to FullContact API communication, 
but understanding Enrich API, webhooks, request and response parameters, 
and common snags is still important.

Once you’re on board with the API behavior, FullContact Client library should simplify 
your integration.

### Supported APIs
- _[Enrich](https://docs.fullcontact.com/docs/enrich-overview)_
    - `person.enrich`
    - `company.enrich`
- Private Identity Cloud
    - _[Resolve](https://docs.fullcontact.com/docs/resolve-overview)_
        - `identity.map`
        - `identity.resolve`
        - `identity.delete`
        - `identity.mapResolve`
    - [Tags](https://docs.fullcontact.com/docs/customer-tags)
        - `tags.create`
        - `tags.get`
        - `tags.delete`
    - [Audience](https://docs.fullcontact.com/docs/customer-tags)
        - `audience.create`
        - `audience.download`
- _[Verify](https://docs.fullcontact.com/docs/verify-overview)_
    - `verify.signals`
    - `verify.match`
    - `verify.activity`
- _[Permission](https://docs.fullcontact.com/docs/permission-overview)_
    - `permission.create`
    - `permission.delete`
    - `permission.find`
    - `permission.current`
    - `permission.verify`