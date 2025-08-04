# stackit-sdk-iaas

IaaS-API

- API version: 1

This API allows you to create and modify IaaS resources.

For more information, please visit [https://support.stackit.cloud/servicedesk](https://support.stackit.cloud/servicedesk)

This package is part of the STACKIT Java SDK. For additional information, please visit the [GitHub repository](https://github.com/stackitcloud/stackit-sdk-java) of the SDK.


## Requirements

Building the API client library requires:
1. Java SDK (version 11 to 21 should be supported) installed on your system

## Installation

To install the API client library to your local Maven repository, simply execute:

```shell
./gradlew publishToMavenLocal
```

To deploy it to a remote Maven repository instead, configure the settings of the repository and execute:

```shell
# TODO: follow up story
# ./gradlew publishToMavenCentral
```

Refer to the [OSSRH Guide](http://central.sonatype.org/pages/ossrh-guide.html) for more information.

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
  <groupId>cloud.stackit</groupId>
  <artifactId>stackit-sdk-iaas</artifactId>
  <version><SDK_VERSION></version>
  <scope>compile</scope>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
  repositories {
    mavenCentral()     // Needed if the 'stackit-sdk-iaas' jar has been published to maven central.
    mavenLocal()       // Needed if the 'stackit-sdk-iaas' jar has been published to the local maven repo.
  }

  dependencies {
     implementation "cloud.stackit:stackit-sdk-iaas:<SDK_VERSION>"
  }
```

### Others

At first generate the JAR by executing:

```shell
mvn clean package
```

Then manually install the following JARs:

- `target/stackit-sdk-iaas-<SDK_VERSION>.jar`
- `target/lib/*.jar`

## Getting Started

Please follow the [installation](#installation) instruction and execute the following Java code:

```java

import cloud.stackit.sdk.iaas.*;
import cloud.stackit.sdk.iaas.auth.*;
import cloud.stackit.sdk.iaas.model.*;
import cloud.stackit.sdk.iaas.api.DefaultApi;

public class DefaultApiExample {

    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://iaas.api.eu01.stackit.cloud");
        
        DefaultApi apiInstance = new DefaultApi(defaultClient);
        UUID projectId = UUID.randomUUID(); // UUID | The identifier (ID) of a STACKIT Project.
        UUID serverId = UUID.randomUUID(); // UUID | The identifier (ID) of a STACKIT Server.
        UUID networkId = UUID.randomUUID(); // UUID | The identifier (ID) of a STACKIT Network.
        try {
            apiInstance.addNetworkToServer(projectId, serverId, networkId);
        } catch (ApiException e) {
            System.err.println("Exception when calling DefaultApi#addNetworkToServer");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}

```

## Recommendation

It's recommended to create an instance of `ApiClient` per thread in a multithreaded environment to avoid any potential issues.
