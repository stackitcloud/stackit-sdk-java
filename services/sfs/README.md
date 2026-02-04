# STACKIT Java SDK for STACKIT File Storage (SFS)

- API version: 1.0.0

API used to create and manage NFS Shares.


This package is part of the STACKIT Java SDK. For additional information, please visit the [GitHub repository](https://github.com/stackitcloud/stackit-sdk-java) of the SDK.

## Installation from Maven Central (recommended)

The release artifacts for this SDK submodule are available on [Maven Central](https://central.sonatype.com/artifact/cloud.stackit.sdk/sfs).

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
  <groupId>cloud.stackit.sdk</groupId>
  <artifactId>sfs</artifactId>
  <version><SDK_VERSION></version>
  <scope>compile</scope>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
  repositories {
    mavenCentral()
  }

  dependencies {
     implementation "cloud.stackit.sdk:sfs:<SDK_VERSION>"
  }
```

## Installation from local build

Building the API client library requires:
1. Java SDK (version 11 to 21 should be supported) installed on your system

To install the API client library to your local Maven repository, simply execute:

```shell
./gradlew services:sfs:publishToMavenLocal
```

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
  <groupId>cloud.stackit.sdk</groupId>
  <artifactId>sfs</artifactId>
  <version><SDK_VERSION></version>
  <scope>compile</scope>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
  repositories {
    mavenLocal()
  }

  dependencies {
     implementation "cloud.stackit.sdk:sfs:<SDK_VERSION>"
  }
```

## Getting Started

See the [sfs examples](https://github.com/stackitcloud/stackit-sdk-java/tree/main/examples/sfs/src/main/java/cloud/stackit/sdk/sfs/examples).

## Recommendation

It's recommended to create an instance of `ApiClient` per thread in a multithreaded environment to avoid any potential issues.
