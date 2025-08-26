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

See the [iaas examples](https://github.com/stackitcloud/stackit-sdk-java/tree/main/examples/iaas/src/main/java/cloud/stackit/sdk/iaas/examples).


## Recommendation

It's recommended to create an instance of `ApiClient` per thread in a multithreaded environment to avoid any potential issues.
