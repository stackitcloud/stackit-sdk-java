

# CreateFolderPayload


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**containerParentId** | **String** | Identifier of the parent resource container - containerId as well as UUID identifier is supported. |  |
|**labels** | **Map&lt;String, String&gt;** | Labels are key-value string pairs that can be attached to a resource container. Some labels may be enforced via policies.  - A label key must match the regex &#x60;[A-ZÄÜÖa-zäüöß0-9_-]{1,64}&#x60;. - A label value must match the regex &#x60;^$|[A-ZÄÜÖa-zäüöß0-9_-]{1,64}&#x60;. |  [optional] |
|**members** | [**Set&lt;Member&gt;**](Member.md) | The initial members assigned to the project. At least one subject needs to be a user, and not a client or service account. |  [optional] |
|**name** | **String** | The name of the folder matching the regex &#x60;^[a-zA-ZäüöÄÜÖ0-9]( ?[a-zA-ZäüöÄÜÖß0-9_+&amp;-]){0,39}$&#x60;. |  |



