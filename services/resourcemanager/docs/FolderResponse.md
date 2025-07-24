

# FolderResponse


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**containerId** | **String** | Globally unique, user-friendly identifier. |  |
|**creationTime** | **OffsetDateTime** | Timestamp at which the folder was created. |  |
|**folderId** | **UUID** | Globally unique folder identifier. |  |
|**labels** | **Map&lt;String, String&gt;** | Labels are key-value string pairs that can be attached to a resource container. Some labels may be enforced via policies.  - A label key must match the regex &#x60;[A-ZÄÜÖa-zäüöß0-9_-]{1,64}&#x60;. - A label value must match the regex &#x60;^$|[A-ZÄÜÖa-zäüöß0-9_-]{1,64}&#x60;. |  [optional] |
|**name** | **String** | Folder name. |  |
|**parent** | [**Parent**](Parent.md) |  |  |
|**updateTime** | **OffsetDateTime** | Timestamp at which the folder was last modified. |  |



