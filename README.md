<div align="center">
<br>
<img src=".github/images/stackit-logo.svg" alt="STACKIT logo" width="50%"/>
<br>
<br>
</div>

# STACKIT SDK for Java (BETA)

[![GitHub License](https://img.shields.io/github/license/stackitcloud/stackit-sdk-java)](https://www.apache.org/licenses/LICENSE-2.0)
[![CI/CD](https://github.com/stackitcloud/stackit-sdk-java/actions/workflows/ci.yaml/badge.svg?branch=main)](https://github.com/stackitcloud/stackit-sdk-java/actions/workflows/ci.yaml)

This repository contains the STACKIT SDKs for Java.

## Getting started

Requires Java 8 or higher.

The release artifacts of the STACKIT Java SDK are available on [Maven Central](https://central.sonatype.com/namespace/cloud.stackit.sdk). 
See below how to use them in your Java project.

### Maven

Add the dependencies for the services you want to interact with to your project's POM, e.g. `iaas` and `resourcemanager` (replace `<SDK_VERSION>` with the latest version of each SDK submdoule): 

```xml
<dependency>
  <groupId>cloud.stackit.sdk</groupId>
  <artifactId>iaas</artifactId>
  <version><SDK_VERSION></version>
  <scope>compile</scope>
</dependency>
<dependency>
  <groupId>cloud.stackit.sdk</groupId>
  <artifactId>resourcemanager</artifactId>
  <version><SDK_VERSION></version>
  <scope>compile</scope>
</dependency>
```

### Gradle

Add the dependencies to your project's build file (replace `<SDK_VERSION>` with the latest version of each SDK submdoule):

```groovy
  repositories {
    mavenCentral()
  }

  dependencies {
     // add the dependencies of the services you want to interact with here,
     // e.g. "iaas" and "resourcemanager"
     implementation "cloud.stackit.sdk:iaas:<SDK_VERSION>"
     implementation "cloud.stackit.sdk:resourcemanger:<SDK_VERSION>"
  }
```

## Examples

Examples on services, configuration and authentication possibilities can be found in the [examples folder](https://github.com/stackitcloud/stackit-sdk-java/tree/main/examples).

## Reporting issues

If you encounter any issues or have suggestions for improvements, please open an issue in the repository or create a ticket in the [STACKIT Help Center](https://support.stackit.cloud/).

## Contribute

Your contribution is welcome! For more details on how to contribute, refer to our [Contribution Guide](./CONTRIBUTION.md).

## Release creation

See the [release documentation](./RELEASE.md) for further information.

## License

Apache 2.0
