# STACKIT Java SDK for STACKIT Application Load Balancer API

- API version: 2.0.0

This API offers an interface to provision and manage Application Load Balancers in your STACKIT project.This solution offers modern L7 load balancing. Current features include TLS, path and prefix based routing aswell as routing based on headers, query parameters and keeping connections persistent with cookies and web sockets.

For each Application Load Balancer provided, two VMs are deployed in your STACKIT project and are subject to fees.


This package is part of the STACKIT Java SDK. For additional information, please visit the [GitHub repository](https://github.com/stackitcloud/stackit-sdk-java) of the SDK.

## Installation from Maven Central (recommended)

The release artifacts for this SDK submodule are available on [Maven Central](https://central.sonatype.com/artifact/cloud.stackit.sdk/alb).

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
  <groupId>cloud.stackit.sdk</groupId>
  <artifactId>alb</artifactId>
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
     implementation "cloud.stackit.sdk:alb:<SDK_VERSION>"
  }
```

## Installation from local build

Building the API client library requires:
1. Java SDK (version 11 to 21 should be supported) installed on your system

To install the API client library to your local Maven repository, simply execute:

```shell
./gradlew services:alb:publishToMavenLocal
```

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
  <groupId>cloud.stackit.sdk</groupId>
  <artifactId>alb</artifactId>
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
     implementation "cloud.stackit.sdk:alb:<SDK_VERSION>"
  }
```

## Getting Started

See the [alb examples](https://github.com/stackitcloud/stackit-sdk-java/tree/main/examples/alb/src/main/java/cloud/stackit/sdk/alb/examples).

## Recommendation

It's recommended to create an instance of `ApiClient` per thread in a multithreaded environment to avoid any potential issues.
