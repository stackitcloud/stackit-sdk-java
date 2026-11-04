# STACKIT Java SDK for STACKIT Key Management Service API

This package is part of the STACKIT Java SDK. For additional information, please visit the [GitHub repository](https://github.com/stackitcloud/stackit-sdk-java) of the SDK.

## Installation from Maven Central (recommended)

The release artifacts for this SDK submodule are available on [Maven Central](https://central.sonatype.com/artifact/cloud.stackit.sdk/kms).

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
  <groupId>cloud.stackit.sdk</groupId>
  <artifactId>kms</artifactId>
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
     implementation "cloud.stackit.sdk:kms:<SDK_VERSION>"
  }
```

## Installation from local build

Building the API client library requires:
1. Java SDK (version 11 to 21 should be supported) installed on your system

To install the API client library to your local Maven repository, simply execute:

```shell
./gradlew services:kms:publishToMavenLocal
```

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
  <groupId>cloud.stackit.sdk</groupId>
  <artifactId>kms</artifactId>
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
     implementation "cloud.stackit.sdk:kms:<SDK_VERSION>"
  }
```

## Getting Started

See the [kms examples](https://github.com/stackitcloud/stackit-sdk-java/tree/main/examples/kms/src/main/java/cloud/stackit/sdk/kms/examples).

## Recommendation

It's recommended to create an instance of `ApiClient` per thread in a multithreaded environment to avoid any potential issues.
