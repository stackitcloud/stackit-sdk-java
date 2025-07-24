# stackit-sdk-resourcemanager

Resource Manager API

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


## Requirements

Building the API client library requires:
1. Java 1.8+

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
  <artifactId>stackit-sdk-resourcemanager</artifactId>
  <version><SDK_VERSION></version>
  <scope>compile</scope>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
  repositories {
    mavenCentral()     // Needed if the 'stackit-sdk-resourcemanager' jar has been published to maven central.
    mavenLocal()       // Needed if the 'stackit-sdk-resourcemanager' jar has been published to the local maven repo.
  }

  dependencies {
     implementation "cloud.stackit:stackit-sdk-resourcemanager:<SDK_VERSION>"
  }
```

### Others

At first generate the JAR by executing:

```shell
mvn clean package
```

Then manually install the following JARs:

- `target/stackit-sdk-resourcemanager-<SDK_VERSION>.jar`
- `target/lib/*.jar`

## Getting Started

Please follow the [installation](#installation) instruction and execute the following Java code:

```java

import cloud.stackit.sdk.resourcemanager.*;
import cloud.stackit.sdk.resourcemanager.auth.*;
import cloud.stackit.sdk.resourcemanager.model.*;
import cloud.stackit.sdk.resourcemanager.api.DefaultApi;

public class DefaultApiExample {

    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://resource-manager.api.stackit.cloud");
        
        DefaultApi apiInstance = new DefaultApi(defaultClient);
        CreateFolderPayload createFolderPayload = new CreateFolderPayload(); // CreateFolderPayload | 
        try {
            FolderResponse result = apiInstance.createFolder(createFolderPayload);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DefaultApi#createFolder");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}

```

## Documentation for API Endpoints

All URIs are relative to *https://resource-manager.api.stackit.cloud*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*DefaultApi* | [**createFolder**](docs/DefaultApi.md#createFolder) | **POST** /v2/folders | Create Folder
*DefaultApi* | [**createProject**](docs/DefaultApi.md#createProject) | **POST** /v2/projects | Create Project
*DefaultApi* | [**deleteFolder**](docs/DefaultApi.md#deleteFolder) | **DELETE** /v2/folders/{containerId} | Delete Folder
*DefaultApi* | [**deleteFolderLabels**](docs/DefaultApi.md#deleteFolderLabels) | **DELETE** /v2/folders/{containerId}/labels | Delete Folder Labels
*DefaultApi* | [**deleteOrganizationLabels**](docs/DefaultApi.md#deleteOrganizationLabels) | **DELETE** /v2/organizations/{containerId}/labels | Delete Organization Labels
*DefaultApi* | [**deleteProject**](docs/DefaultApi.md#deleteProject) | **DELETE** /v2/projects/{id} | Delete Project
*DefaultApi* | [**deleteProjectLabels**](docs/DefaultApi.md#deleteProjectLabels) | **DELETE** /v2/projects/{containerId}/labels | Delete Project Labels
*DefaultApi* | [**getFolderDetails**](docs/DefaultApi.md#getFolderDetails) | **GET** /v2/folders/{containerId} | Get Folder Details
*DefaultApi* | [**getOrganization**](docs/DefaultApi.md#getOrganization) | **GET** /v2/organizations/{id} | Get Organization Details
*DefaultApi* | [**getProject**](docs/DefaultApi.md#getProject) | **GET** /v2/projects/{id} | Get Project Details
*DefaultApi* | [**listFolders**](docs/DefaultApi.md#listFolders) | **GET** /v2/folders | Get All Folders
*DefaultApi* | [**listOrganizations**](docs/DefaultApi.md#listOrganizations) | **GET** /v2/organizations | Get All Organizations
*DefaultApi* | [**listProjects**](docs/DefaultApi.md#listProjects) | **GET** /v2/projects | Get All Projects
*DefaultApi* | [**partialUpdateFolder**](docs/DefaultApi.md#partialUpdateFolder) | **PATCH** /v2/folders/{containerId} | Update Folder
*DefaultApi* | [**partialUpdateOrganization**](docs/DefaultApi.md#partialUpdateOrganization) | **PATCH** /v2/organizations/{id} | Update Organization
*DefaultApi* | [**partialUpdateProject**](docs/DefaultApi.md#partialUpdateProject) | **PATCH** /v2/projects/{id} | Update Project


## Documentation for Models

 - [CreateFolderPayload](docs/CreateFolderPayload.md)
 - [CreateProjectPayload](docs/CreateProjectPayload.md)
 - [ErrorResponse](docs/ErrorResponse.md)
 - [FolderResponse](docs/FolderResponse.md)
 - [GetFolderDetailsResponse](docs/GetFolderDetailsResponse.md)
 - [GetProjectResponse](docs/GetProjectResponse.md)
 - [LifecycleState](docs/LifecycleState.md)
 - [ListFoldersResponse](docs/ListFoldersResponse.md)
 - [ListFoldersResponseItemsInner](docs/ListFoldersResponseItemsInner.md)
 - [ListOrganizationsResponse](docs/ListOrganizationsResponse.md)
 - [ListOrganizationsResponseItemsInner](docs/ListOrganizationsResponseItemsInner.md)
 - [ListProjectsResponse](docs/ListProjectsResponse.md)
 - [Member](docs/Member.md)
 - [OrganizationResponse](docs/OrganizationResponse.md)
 - [Parent](docs/Parent.md)
 - [ParentListInner](docs/ParentListInner.md)
 - [PartialUpdateFolderPayload](docs/PartialUpdateFolderPayload.md)
 - [PartialUpdateOrganizationPayload](docs/PartialUpdateOrganizationPayload.md)
 - [PartialUpdateProjectPayload](docs/PartialUpdateProjectPayload.md)
 - [Project](docs/Project.md)


## Recommendation

It's recommended to create an instance of `ApiClient` per thread in a multithreaded environment to avoid any potential issues.
