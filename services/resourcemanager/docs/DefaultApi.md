# DefaultApi

All URIs are relative to *https://resource-manager.api.stackit.cloud*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createFolder**](DefaultApi.md#createFolder) | **POST** /v2/folders | Create Folder |
| [**createProject**](DefaultApi.md#createProject) | **POST** /v2/projects | Create Project |
| [**deleteFolder**](DefaultApi.md#deleteFolder) | **DELETE** /v2/folders/{containerId} | Delete Folder |
| [**deleteFolderLabels**](DefaultApi.md#deleteFolderLabels) | **DELETE** /v2/folders/{containerId}/labels | Delete Folder Labels |
| [**deleteOrganizationLabels**](DefaultApi.md#deleteOrganizationLabels) | **DELETE** /v2/organizations/{containerId}/labels | Delete Organization Labels |
| [**deleteProject**](DefaultApi.md#deleteProject) | **DELETE** /v2/projects/{id} | Delete Project |
| [**deleteProjectLabels**](DefaultApi.md#deleteProjectLabels) | **DELETE** /v2/projects/{containerId}/labels | Delete Project Labels |
| [**getFolderDetails**](DefaultApi.md#getFolderDetails) | **GET** /v2/folders/{containerId} | Get Folder Details |
| [**getOrganization**](DefaultApi.md#getOrganization) | **GET** /v2/organizations/{id} | Get Organization Details |
| [**getProject**](DefaultApi.md#getProject) | **GET** /v2/projects/{id} | Get Project Details |
| [**listFolders**](DefaultApi.md#listFolders) | **GET** /v2/folders | Get All Folders |
| [**listOrganizations**](DefaultApi.md#listOrganizations) | **GET** /v2/organizations | Get All Organizations |
| [**listProjects**](DefaultApi.md#listProjects) | **GET** /v2/projects | Get All Projects |
| [**partialUpdateFolder**](DefaultApi.md#partialUpdateFolder) | **PATCH** /v2/folders/{containerId} | Update Folder |
| [**partialUpdateOrganization**](DefaultApi.md#partialUpdateOrganization) | **PATCH** /v2/organizations/{id} | Update Organization |
| [**partialUpdateProject**](DefaultApi.md#partialUpdateProject) | **PATCH** /v2/projects/{id} | Update Project |


<a id="createFolder"></a>
# **createFolder**
> FolderResponse createFolder(createFolderPayload)

Create Folder

Create a new folder.

### Example
```java
// Import classes:
import cloud.stackit.sdk.resourcemanager.ApiClient;
import cloud.stackit.sdk.resourcemanager.ApiException;
import cloud.stackit.sdk.resourcemanager.Configuration;
import cloud.stackit.sdk.resourcemanager.models.*;
import cloud.stackit.sdk.resourcemanager.api.DefaultApi;

public class Example {
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

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **createFolderPayload** | [**CreateFolderPayload**](CreateFolderPayload.md)|  | [optional] |

### Return type

[**FolderResponse**](FolderResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Created |  -  |
| **400** | Bad Request |  -  |
| **403** | Forbidden |  -  |
| **409** | Conflict |  -  |

<a id="createProject"></a>
# **createProject**
> Project createProject(createProjectPayload)

Create Project

Create a new project.  - The request is synchronous, but the workflow-based creation is asynchronous. - Lifecycle state remains in CREATING, until workflow completes

### Example
```java
// Import classes:
import cloud.stackit.sdk.resourcemanager.ApiClient;
import cloud.stackit.sdk.resourcemanager.ApiException;
import cloud.stackit.sdk.resourcemanager.Configuration;
import cloud.stackit.sdk.resourcemanager.models.*;
import cloud.stackit.sdk.resourcemanager.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://resource-manager.api.stackit.cloud");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    CreateProjectPayload createProjectPayload = new CreateProjectPayload(); // CreateProjectPayload | 
    try {
      Project result = apiInstance.createProject(createProjectPayload);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#createProject");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **createProjectPayload** | [**CreateProjectPayload**](CreateProjectPayload.md)|  | [optional] |

### Return type

[**Project**](Project.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Project created |  -  |
| **400** | Bad Request |  -  |
| **403** | Forbidden |  -  |
| **409** | Conflict |  -  |

<a id="deleteFolder"></a>
# **deleteFolder**
> deleteFolder(containerId, force)

Delete Folder

Delete a folder and its metadata. - Folder must not be parent of any other container - A force flag may be set, deleting all underlying folders recursively - if no project is attached!

### Example
```java
// Import classes:
import cloud.stackit.sdk.resourcemanager.ApiClient;
import cloud.stackit.sdk.resourcemanager.ApiException;
import cloud.stackit.sdk.resourcemanager.Configuration;
import cloud.stackit.sdk.resourcemanager.models.*;
import cloud.stackit.sdk.resourcemanager.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://resource-manager.api.stackit.cloud");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    String containerId = "containerId_example"; // String | Folder identifier - containerId as well as UUID identifier is supported.
    Boolean force = false; // Boolean | If true, all nested, empty folders are deleted recursively - if no project is attached!
    try {
      apiInstance.deleteFolder(containerId, force);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#deleteFolder");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **containerId** | **String**| Folder identifier - containerId as well as UUID identifier is supported. | |
| **force** | **Boolean**| If true, all nested, empty folders are deleted recursively - if no project is attached! | [optional] [default to false] |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **202** | Deletion process triggered |  -  |
| **404** | Not Found |  -  |
| **409** | Conflict |  -  |

<a id="deleteFolderLabels"></a>
# **deleteFolderLabels**
> deleteFolderLabels(containerId, keys)

Delete Folder Labels

Deletes all folder labels by given keys. - Specific labels may be deleted by key(s) - If no key is specified, all labels will be deleted!

### Example
```java
// Import classes:
import cloud.stackit.sdk.resourcemanager.ApiClient;
import cloud.stackit.sdk.resourcemanager.ApiException;
import cloud.stackit.sdk.resourcemanager.Configuration;
import cloud.stackit.sdk.resourcemanager.models.*;
import cloud.stackit.sdk.resourcemanager.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://resource-manager.api.stackit.cloud");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    String containerId = "containerId_example"; // String | Folder identifier - containerId as well as UUID identifier is supported.
    List<String> keys = Arrays.asList(); // List<String> | Label name.
    try {
      apiInstance.deleteFolderLabels(containerId, keys);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#deleteFolderLabels");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **containerId** | **String**| Folder identifier - containerId as well as UUID identifier is supported. | |
| **keys** | [**List&lt;String&gt;**](String.md)| Label name. | [optional] |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **202** | Labels removed |  -  |
| **409** | Conflict |  -  |

<a id="deleteOrganizationLabels"></a>
# **deleteOrganizationLabels**
> deleteOrganizationLabels(containerId, keys)

Delete Organization Labels

Deletes all organization labels by given keys. - Specific labels may be deleted by key(s) - If no key is specified, all labels will be deleted!

### Example
```java
// Import classes:
import cloud.stackit.sdk.resourcemanager.ApiClient;
import cloud.stackit.sdk.resourcemanager.ApiException;
import cloud.stackit.sdk.resourcemanager.Configuration;
import cloud.stackit.sdk.resourcemanager.models.*;
import cloud.stackit.sdk.resourcemanager.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://resource-manager.api.stackit.cloud");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    String containerId = "containerId_example"; // String | Organization identifier - containerId as well as UUID identifier is supported.
    List<String> keys = Arrays.asList(); // List<String> | Label name.
    try {
      apiInstance.deleteOrganizationLabels(containerId, keys);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#deleteOrganizationLabels");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **containerId** | **String**| Organization identifier - containerId as well as UUID identifier is supported. | |
| **keys** | [**List&lt;String&gt;**](String.md)| Label name. | [optional] |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **202** | Labels removed |  -  |
| **409** | Conflict |  -  |

<a id="deleteProject"></a>
# **deleteProject**
> deleteProject(id)

Delete Project

Triggers the deletion of a project.  - The request is synchronous, but the workflow-based deletion is asynchronous - Lifecycle state remains in DELETING, until workflow completes

### Example
```java
// Import classes:
import cloud.stackit.sdk.resourcemanager.ApiClient;
import cloud.stackit.sdk.resourcemanager.ApiException;
import cloud.stackit.sdk.resourcemanager.Configuration;
import cloud.stackit.sdk.resourcemanager.models.*;
import cloud.stackit.sdk.resourcemanager.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://resource-manager.api.stackit.cloud");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    String id = "id_example"; // String | Project identifier - containerId as well as UUID identifier is supported.
    try {
      apiInstance.deleteProject(id);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#deleteProject");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**| Project identifier - containerId as well as UUID identifier is supported. | |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **202** | Deletion process triggered |  -  |
| **409** | Conflict |  -  |

<a id="deleteProjectLabels"></a>
# **deleteProjectLabels**
> deleteProjectLabels(containerId, keys)

Delete Project Labels

Deletes all project labels by given keys. - Specific labels may be deleted by key(s) - If no key is specified, all labels will be deleted!

### Example
```java
// Import classes:
import cloud.stackit.sdk.resourcemanager.ApiClient;
import cloud.stackit.sdk.resourcemanager.ApiException;
import cloud.stackit.sdk.resourcemanager.Configuration;
import cloud.stackit.sdk.resourcemanager.models.*;
import cloud.stackit.sdk.resourcemanager.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://resource-manager.api.stackit.cloud");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    String containerId = "containerId_example"; // String | Project identifier - containerId as well as UUID identifier is supported.
    List<String> keys = Arrays.asList(); // List<String> | Label name.
    try {
      apiInstance.deleteProjectLabels(containerId, keys);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#deleteProjectLabels");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **containerId** | **String**| Project identifier - containerId as well as UUID identifier is supported. | |
| **keys** | [**List&lt;String&gt;**](String.md)| Label name. | [optional] |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **202** | Labels removed |  -  |
| **409** | Conflict |  -  |

<a id="getFolderDetails"></a>
# **getFolderDetails**
> GetFolderDetailsResponse getFolderDetails(containerId, includeParents)

Get Folder Details

Returns all metadata for a specific folder.

### Example
```java
// Import classes:
import cloud.stackit.sdk.resourcemanager.ApiClient;
import cloud.stackit.sdk.resourcemanager.ApiException;
import cloud.stackit.sdk.resourcemanager.Configuration;
import cloud.stackit.sdk.resourcemanager.models.*;
import cloud.stackit.sdk.resourcemanager.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://resource-manager.api.stackit.cloud");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    String containerId = "containerId_example"; // String | Folder identifier - containerId as well as UUID identifier is supported.
    Boolean includeParents = false; // Boolean | 
    try {
      GetFolderDetailsResponse result = apiInstance.getFolderDetails(containerId, includeParents);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#getFolderDetails");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **containerId** | **String**| Folder identifier - containerId as well as UUID identifier is supported. | |
| **includeParents** | **Boolean**|  | [optional] [default to false] |

### Return type

[**GetFolderDetailsResponse**](GetFolderDetailsResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **403** | Forbidden |  -  |
| **404** | Not Found |  -  |

<a id="getOrganization"></a>
# **getOrganization**
> OrganizationResponse getOrganization(id)

Get Organization Details

Returns the organization and its metadata.

### Example
```java
// Import classes:
import cloud.stackit.sdk.resourcemanager.ApiClient;
import cloud.stackit.sdk.resourcemanager.ApiException;
import cloud.stackit.sdk.resourcemanager.Configuration;
import cloud.stackit.sdk.resourcemanager.models.*;
import cloud.stackit.sdk.resourcemanager.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://resource-manager.api.stackit.cloud");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    String id = "id_example"; // String | Organization identifier - containerId as well as UUID identifier is supported.
    try {
      OrganizationResponse result = apiInstance.getOrganization(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#getOrganization");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**| Organization identifier - containerId as well as UUID identifier is supported. | |

### Return type

[**OrganizationResponse**](OrganizationResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **404** | Not Found |  -  |

<a id="getProject"></a>
# **getProject**
> GetProjectResponse getProject(id, includeParents)

Get Project Details

Returns the project and its metadata.

### Example
```java
// Import classes:
import cloud.stackit.sdk.resourcemanager.ApiClient;
import cloud.stackit.sdk.resourcemanager.ApiException;
import cloud.stackit.sdk.resourcemanager.Configuration;
import cloud.stackit.sdk.resourcemanager.models.*;
import cloud.stackit.sdk.resourcemanager.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://resource-manager.api.stackit.cloud");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    String id = "id_example"; // String | Project identifier - containerId as well as UUID identifier is supported.
    Boolean includeParents = false; // Boolean | 
    try {
      GetProjectResponse result = apiInstance.getProject(id, includeParents);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#getProject");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**| Project identifier - containerId as well as UUID identifier is supported. | |
| **includeParents** | **Boolean**|  | [optional] [default to false] |

### Return type

[**GetProjectResponse**](GetProjectResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **403** | Forbidden |  -  |
| **404** | Not Found |  -  |

<a id="listFolders"></a>
# **listFolders**
> ListFoldersResponse listFolders(containerParentId, containerIds, member, limit, offset, creationTimeStart)

Get All Folders

Returns all folders and their metadata that:  - Are children of the specific containerParentId - Match the given containerIds - User is member of &lt;br /&gt;  Filter: - Either containerParentId OR containerIds OR member must be passed - If containerId and containerParentId are given, both are used for filtering - containers must point to the same parent - If member and containerParentId are given, both are used for filtering - If member is given, containers must not point to the same container parent 

### Example
```java
// Import classes:
import cloud.stackit.sdk.resourcemanager.ApiClient;
import cloud.stackit.sdk.resourcemanager.ApiException;
import cloud.stackit.sdk.resourcemanager.Configuration;
import cloud.stackit.sdk.resourcemanager.models.*;
import cloud.stackit.sdk.resourcemanager.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://resource-manager.api.stackit.cloud");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    String containerParentId = "containerParentId_example"; // String | Identifier of the parent resource container - containerId as well as UUID identifier is supported.
    List<String> containerIds = Arrays.asList(); // List<String> | List of container identifiers - containerId as well as UUID identifier is supported.
    String member = "member_example"; // String | E-Mail address of the user for whom the visible resource containers should be filtered.
    BigDecimal limit = new BigDecimal("50"); // BigDecimal | The maximum number of projects to return in the response. If not present, an appropriate default will be used. If maximum is exceeded, maximum is used.
    BigDecimal offset = new BigDecimal("0"); // BigDecimal | The offset of the first item in the collection to return.
    OffsetDateTime creationTimeStart = OffsetDateTime.parse("2021-01-20T00:00Z"); // OffsetDateTime | A timestamp to specify the beginning of the creationTime from which entries should be returned. If not given, defaults to the beginning of time.
    try {
      ListFoldersResponse result = apiInstance.listFolders(containerParentId, containerIds, member, limit, offset, creationTimeStart);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#listFolders");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **containerParentId** | **String**| Identifier of the parent resource container - containerId as well as UUID identifier is supported. | [optional] |
| **containerIds** | [**List&lt;String&gt;**](String.md)| List of container identifiers - containerId as well as UUID identifier is supported. | [optional] |
| **member** | **String**| E-Mail address of the user for whom the visible resource containers should be filtered. | [optional] |
| **limit** | **BigDecimal**| The maximum number of projects to return in the response. If not present, an appropriate default will be used. If maximum is exceeded, maximum is used. | [optional] [default to 50] |
| **offset** | **BigDecimal**| The offset of the first item in the collection to return. | [optional] [default to 0] |
| **creationTimeStart** | **OffsetDateTime**| A timestamp to specify the beginning of the creationTime from which entries should be returned. If not given, defaults to the beginning of time. | [optional] |

### Return type

[**ListFoldersResponse**](ListFoldersResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **400** | Bad Request |  -  |
| **403** | Forbidden |  -  |
| **409** | Conflict |  -  |

<a id="listOrganizations"></a>
# **listOrganizations**
> ListOrganizationsResponse listOrganizations(containerIds, member, limit, offset, creationTimeStart)

Get All Organizations

Returns all organizations and their metadata. - If no containerIds are specified, all organizations are returned, if permitted - ContainerIds may be set to filter - Member may be set to filter - If member and containerIds are given, both are used for filtering 

### Example
```java
// Import classes:
import cloud.stackit.sdk.resourcemanager.ApiClient;
import cloud.stackit.sdk.resourcemanager.ApiException;
import cloud.stackit.sdk.resourcemanager.Configuration;
import cloud.stackit.sdk.resourcemanager.models.*;
import cloud.stackit.sdk.resourcemanager.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://resource-manager.api.stackit.cloud");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    List<String> containerIds = Arrays.asList(); // List<String> | Organization identifiers - containerId as well as UUID identifier is supported. A combination of both is not allowed.
    String member = "member_example"; // String | E-Mail address of the user for whom the visible resource containers should be filtered.
    BigDecimal limit = new BigDecimal("50"); // BigDecimal | The maximum number of projects to return in the response. If not present, an appropriate default will be used. If maximum is exceeded, maximum is used.
    BigDecimal offset = new BigDecimal("0"); // BigDecimal | The offset of the first item in the collection to return.
    OffsetDateTime creationTimeStart = OffsetDateTime.parse("2021-01-20T00:00Z"); // OffsetDateTime | A timestamp to specify the beginning of the creationTime from which entries should be returned. If not given, defaults to the beginning of time.
    try {
      ListOrganizationsResponse result = apiInstance.listOrganizations(containerIds, member, limit, offset, creationTimeStart);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#listOrganizations");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **containerIds** | [**List&lt;String&gt;**](String.md)| Organization identifiers - containerId as well as UUID identifier is supported. A combination of both is not allowed. | [optional] |
| **member** | **String**| E-Mail address of the user for whom the visible resource containers should be filtered. | [optional] |
| **limit** | **BigDecimal**| The maximum number of projects to return in the response. If not present, an appropriate default will be used. If maximum is exceeded, maximum is used. | [optional] [default to 50] |
| **offset** | **BigDecimal**| The offset of the first item in the collection to return. | [optional] [default to 0] |
| **creationTimeStart** | **OffsetDateTime**| A timestamp to specify the beginning of the creationTime from which entries should be returned. If not given, defaults to the beginning of time. | [optional] |

### Return type

[**ListOrganizationsResponse**](ListOrganizationsResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **400** | Bad Request |  -  |
| **403** | Forbidden |  -  |

<a id="listProjects"></a>
# **listProjects**
> ListProjectsResponse listProjects(containerParentId, containerIds, member, offset, limit, creationTimeStart)

Get All Projects

Returns all projects and their metadata that:  - Are children of the specific containerParentId - Match the given containerIds - User is member of  Filter: - Either containerParentId OR containerIds OR member must be passed - If containerId and containerParentId are given, both are used for filtering - containers must point to the same parent - If member and containerParentId are given, both are used for filtering - If member is given, containers must not point to the same container parent

### Example
```java
// Import classes:
import cloud.stackit.sdk.resourcemanager.ApiClient;
import cloud.stackit.sdk.resourcemanager.ApiException;
import cloud.stackit.sdk.resourcemanager.Configuration;
import cloud.stackit.sdk.resourcemanager.models.*;
import cloud.stackit.sdk.resourcemanager.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://resource-manager.api.stackit.cloud");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    String containerParentId = "containerParentId_example"; // String | Identifier of the parent resource container - containerId as well as UUID identifier is supported.
    List<String> containerIds = Arrays.asList(); // List<String> | List of container identifiers - containerId as well as UUID identifier is supported.
    String member = "member_example"; // String | E-Mail address of the user for whom the visible resource containers should be filtered.
    BigDecimal offset = new BigDecimal("0"); // BigDecimal | The offset of the first item in the collection to return.
    BigDecimal limit = new BigDecimal("50"); // BigDecimal | The maximum number of projects to return in the response. If not present, an appropriate default will be used. If maximum is exceeded, maximum is used.
    OffsetDateTime creationTimeStart = OffsetDateTime.parse("2021-01-20T00:00Z"); // OffsetDateTime | A timestamp to specify the beginning of the creationTime from which entries should be returned. If not given, defaults to the beginning of time.
    try {
      ListProjectsResponse result = apiInstance.listProjects(containerParentId, containerIds, member, offset, limit, creationTimeStart);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#listProjects");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **containerParentId** | **String**| Identifier of the parent resource container - containerId as well as UUID identifier is supported. | [optional] |
| **containerIds** | [**List&lt;String&gt;**](String.md)| List of container identifiers - containerId as well as UUID identifier is supported. | [optional] |
| **member** | **String**| E-Mail address of the user for whom the visible resource containers should be filtered. | [optional] |
| **offset** | **BigDecimal**| The offset of the first item in the collection to return. | [optional] [default to 0] |
| **limit** | **BigDecimal**| The maximum number of projects to return in the response. If not present, an appropriate default will be used. If maximum is exceeded, maximum is used. | [optional] [default to 50] |
| **creationTimeStart** | **OffsetDateTime**| A timestamp to specify the beginning of the creationTime from which entries should be returned. If not given, defaults to the beginning of time. | [optional] |

### Return type

[**ListProjectsResponse**](ListProjectsResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **400** | Bad Request |  -  |
| **403** | Forbidden |  -  |

<a id="partialUpdateFolder"></a>
# **partialUpdateFolder**
> FolderResponse partialUpdateFolder(containerId, partialUpdateFolderPayload)

Update Folder

Update the folder and its metadata. - Update folder name - Update folder labels - Update folder parent (folder or organization)

### Example
```java
// Import classes:
import cloud.stackit.sdk.resourcemanager.ApiClient;
import cloud.stackit.sdk.resourcemanager.ApiException;
import cloud.stackit.sdk.resourcemanager.Configuration;
import cloud.stackit.sdk.resourcemanager.models.*;
import cloud.stackit.sdk.resourcemanager.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://resource-manager.api.stackit.cloud");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    String containerId = "containerId_example"; // String | Folder identifier - containerId as well as UUID identifier is supported.
    PartialUpdateFolderPayload partialUpdateFolderPayload = new PartialUpdateFolderPayload(); // PartialUpdateFolderPayload | 
    try {
      FolderResponse result = apiInstance.partialUpdateFolder(containerId, partialUpdateFolderPayload);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#partialUpdateFolder");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **containerId** | **String**| Folder identifier - containerId as well as UUID identifier is supported. | |
| **partialUpdateFolderPayload** | [**PartialUpdateFolderPayload**](PartialUpdateFolderPayload.md)|  | [optional] |

### Return type

[**FolderResponse**](FolderResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **403** | Forbidden |  -  |
| **404** | Not Found |  -  |
| **409** | Conflict |  -  |

<a id="partialUpdateOrganization"></a>
# **partialUpdateOrganization**
> OrganizationResponse partialUpdateOrganization(id, partialUpdateOrganizationPayload)

Update Organization

Update the organization and its metadata. - Update organization name - Update organization labels

### Example
```java
// Import classes:
import cloud.stackit.sdk.resourcemanager.ApiClient;
import cloud.stackit.sdk.resourcemanager.ApiException;
import cloud.stackit.sdk.resourcemanager.Configuration;
import cloud.stackit.sdk.resourcemanager.models.*;
import cloud.stackit.sdk.resourcemanager.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://resource-manager.api.stackit.cloud");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    String id = "id_example"; // String | Organization identifier - containerId as well as UUID identifier is supported.
    PartialUpdateOrganizationPayload partialUpdateOrganizationPayload = new PartialUpdateOrganizationPayload(); // PartialUpdateOrganizationPayload | 
    try {
      OrganizationResponse result = apiInstance.partialUpdateOrganization(id, partialUpdateOrganizationPayload);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#partialUpdateOrganization");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**| Organization identifier - containerId as well as UUID identifier is supported. | |
| **partialUpdateOrganizationPayload** | [**PartialUpdateOrganizationPayload**](PartialUpdateOrganizationPayload.md)|  | [optional] |

### Return type

[**OrganizationResponse**](OrganizationResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **404** | Not Found |  -  |
| **409** | Conflict |  -  |

<a id="partialUpdateProject"></a>
# **partialUpdateProject**
> Project partialUpdateProject(id, partialUpdateProjectPayload)

Update Project

Update the project and its metadata. - Update project name  - Update project labels  - Update project parent (folder or organization)

### Example
```java
// Import classes:
import cloud.stackit.sdk.resourcemanager.ApiClient;
import cloud.stackit.sdk.resourcemanager.ApiException;
import cloud.stackit.sdk.resourcemanager.Configuration;
import cloud.stackit.sdk.resourcemanager.models.*;
import cloud.stackit.sdk.resourcemanager.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://resource-manager.api.stackit.cloud");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    String id = "id_example"; // String | Project identifier - containerId as well as UUID identifier is supported.
    PartialUpdateProjectPayload partialUpdateProjectPayload = new PartialUpdateProjectPayload(); // PartialUpdateProjectPayload | 
    try {
      Project result = apiInstance.partialUpdateProject(id, partialUpdateProjectPayload);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#partialUpdateProject");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**| Project identifier - containerId as well as UUID identifier is supported. | |
| **partialUpdateProjectPayload** | [**PartialUpdateProjectPayload**](PartialUpdateProjectPayload.md)|  | [optional] |

### Return type

[**Project**](Project.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |
| **403** | Forbidden |  -  |
| **404** | Not Found |  -  |
| **409** | Conflict |  -  |

