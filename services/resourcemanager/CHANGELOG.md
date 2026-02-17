## v0.4.3
- Bump dependency `org.apache.commons:commons-lang3` to `3.18.0`
- Bump dependency `org.openapitools:jackson-databind-nullable` to `0.2.8`

## v0.4.2
- **Improvement:** Support additional properties in models

## v0.4.1
- Bump dependency `cloud.stackit.sdk.core` to v0.4.1

## v0.4.0
- **Feature:** Added waiter for project creation and project deletion

## v0.3.0
- **Feature:** Add `ContainerSearchResult` model class for container search functionality

## v0.2.0
- **Feature:** Support for passing custom OkHttpClient objects
  - `ApiClient`
    - Added constructors with `OkHttpClient` param (recommended for production use)
    - Use new `KeyFlowAuthenticator` `okhttp3.Authenticator` implementation instead of request interceptor for authentication
  - `DefaultApi`: Added constructors with `OkHttpClient` param (recommended for production use)
  - `ResourceManagerApi`: Added constructors with `OkHttpClient` param (recommended for production use)

## v0.1.0
- Initial onboarding of STACKIT Java SDK for Resourcemanager service
