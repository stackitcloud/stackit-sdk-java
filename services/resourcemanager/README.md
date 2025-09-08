# STACKIT Java SDK for Resource Manager API

- API version: 2.0

API v2 to manage resource containers - organizations, folders, projects incl. labels

### Resource Management
STACKIT resource management handles the terms _Organization_, _Folder_, _Project_, _Label_, and the hierarchical structure between them. Technically, organizations, 
folders, and projects are _Resource Containers_ to which a _Label_ can be attached to. The STACKIT _Resource Manager_ provides CRUD endpoints to query and to modify the state.

### Organizations
STACKIT organizations are the base element to create and to use cloud-resources. An organization is bound to one customer account. Organizations have a lifecycle.
- Organizations are always the root node in resource hierarchy and do not have a parent

### Projects
STACKIT projects are needed to use cloud-resources. Projects serve as wrapper for underlying technical structures and processes. Projects have a lifecycle. Projects compared to folders may have different policies.
- Projects are optional, but mandatory for cloud-resource usage
- A project can be created having either an organization, or a folder as parent
- A project must not have a project as parent
- Project names under the same parent must not be unique
- Root organization cannot be changed

### Label
STACKIT labels are key-value pairs including a resource container reference. Labels can be defined and attached freely to resource containers by which resources can be organized and queried.
- Policy-based, immutable labels may exists

For more information, please visit [https://support.stackit.cloud/servicedesk](https://support.stackit.cloud/servicedesk)

This package is part of the STACKIT Java SDK. For additional information, please visit the [GitHub repository](https://github.com/stackitcloud/stackit-sdk-java) of the SDK.

## Installation from Maven Central (recommended)

The release artifacts for this SDK submodule are available on [Maven Central](https://central.sonatype.com/artifact/cloud.stackit.sdk/resourcemanager).

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
  <groupId>cloud.stackit.sdk</groupId>
  <artifactId>resourcemanager</artifactId>
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
     implementation "cloud.stackit.sdk:resourcemanager:<SDK_VERSION>"
  }
```

## Installation from local build

Building the API client library requires:
1. Java SDK (version 11 to 21 should be supported) installed on your system

To install the API client library to your local Maven repository, simply execute:

```shell
./gradlew services:resourcemanager:publishToMavenLocal
```

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
  <groupId>cloud.stackit.sdk</groupId>
  <artifactId>resourcemanager</artifactId>
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
     implementation "cloud.stackit.sdk:resourcemanager:<SDK_VERSION>"
  }
```

## Getting Started

See the [resourcemanager examples](https://github.com/stackitcloud/stackit-sdk-java/tree/main/examples/resourcemanager/src/main/java/cloud/stackit/sdk/resourcemanager/examples).

## Recommendation

It's recommended to create an instance of `ApiClient` per thread in a multithreaded environment to avoid any potential issues.
