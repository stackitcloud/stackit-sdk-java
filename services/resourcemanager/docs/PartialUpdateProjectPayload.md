

# PartialUpdateProjectPayload


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**containerParentId** | **String** | New parent identifier for the resource container - containerId as well as UUID identifier is supported. |  [optional] |
|**labels** | **Map&lt;String, String&gt;** | Labels are key-value string pairs that can be attached to a resource container. Some labels may be enforced via policies.  - A label key must match the regex &#x60;[A-ZÄÜÖa-zäüöß0-9_-]{1,64}&#x60;. - A label value must match the regex &#x60;^$|[A-ZÄÜÖa-zäüöß0-9_-]{1,64}&#x60;. |  [optional] |
|**name** | **String** | New name for the resource container matching the regex &#x60;^[a-zA-ZäüöÄÜÖ0-9]( ?[a-zA-ZäüöÄÜÖß0-9_+&amp;-]){0,39}$&#x60;. |  [optional] |



