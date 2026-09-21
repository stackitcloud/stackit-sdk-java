## v0.4.0
- `v1api`:
  - **Feature:** New model class `RateLimitError`
  - **Improvement:** Add HTTP 429 rate limit error responses in API operations
  - **Breaking Change:** Field `expires` in `AccessKey` model is now nullable
  - **Breaking Change:** Field `expires` in `CreateAccessKeyResponse` model is now nullable
- `v2api`:
  - **Feature:** New model class `RateLimitError`
  - **Improvement:** Add HTTP 429 rate limit error responses in API operations
  - **Breaking Change:** Field `expires` in `AccessKey` model is now nullable

## v0.3.0
- **Feature (breaking change):** Introduction of multi API version support. See the GitHub discussion post for more details: https://github.com/stackitcloud/stackit-sdk-java/discussions/530

## v0.2.0
- New model classes: `ComplianceLockResponse`, `CredentialsGroupExtended`, `DefaultRetentionResponse`, `DeleteDefaultRetentionResponse`, `GetCredentialsGroupResponse`, `SetDefaultRetentionPayload`
- New methods for `Bucket` model class: `objectLockEnabled`,`setObjectLockEnabled`,`getObjectLockEnabled`
- New enum: `RetentionMode`
- New API client methods: `createComplianceLock`, `deleteComplianceLock`, `deleteDefaultRetention`, `getComplianceLock`, `getCredentialsGroup`, `getDefaultRetention`, `setDefaultRetention`
- **Breaking change:** New parameter `objectLockEnabled` for `createBucket` API client method

## v0.1.3
- Bump dependency `org.apache.commons:commons-lang3` to `3.18.0`
- Bump dependency `org.openapitools:jackson-databind-nullable` to `0.2.8`

## v0.1.2
- **Improvement:** Support additional properties in models

## v0.1.1
- Bump dependency `cloud.stackit.sdk.core` to v0.4.1

## v0.1.0
- Initial onboarding of STACKIT Java SDK for Object storage service
