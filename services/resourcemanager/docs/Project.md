

# Project


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**containerId** | **String** | Globally unique, user-friendly identifier. |  |
|**creationTime** | **OffsetDateTime** | Timestamp at which the project was created. |  |
|**labels** | **Map&lt;String, String&gt;** | Labels are key-value string pairs that can be attached to a resource container. Some labels may be enforced via policies.  - A label key must match the regex &#x60;[A-ZÄÜÖa-zäüöß0-9_-]{1,64}&#x60;. - A label value must match the regex &#x60;^$|[A-ZÄÜÖa-zäüöß0-9_-]{1,64}&#x60;. |  [optional] |
|**lifecycleState** | **LifecycleState** |  |  |
|**name** | **String** | Project name. |  |
|**parent** | [**Parent**](Parent.md) |  |  |
|**projectId** | **UUID** | Globally unique, project identifier. |  |
|**updateTime** | **OffsetDateTime** | Timestamp at which the project was last modified. |  |



