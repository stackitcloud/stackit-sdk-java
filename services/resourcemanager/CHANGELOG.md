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
