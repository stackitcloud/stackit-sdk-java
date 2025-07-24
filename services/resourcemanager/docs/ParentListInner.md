

# ParentListInner


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**containerId** | **String** | User-friendly identifier of either organization or folder (will replace id). |  |
|**containerParentId** | **String** | User-friendly parent identifier of either organization or folder (will replace parentId). |  [optional] |
|**id** | **UUID** | Identifier. |  |
|**name** | **String** | Parent container name. |  |
|**parentId** | **UUID** | Identifier of the parent resource container. |  [optional] |
|**type** | [**TypeEnum**](#TypeEnum) | Parent container type. |  |



## Enum: TypeEnum

| Name | Value |
|---- | -----|
| FOLDER | &quot;FOLDER&quot; |
| ORGANIZATION | &quot;ORGANIZATION&quot; |



