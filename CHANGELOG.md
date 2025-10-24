## Release (2025-xx-xx)
- `core`: [v0.3.0](core/CHANGELOG.md#v030)
  - **Feature:** New exception types for better error handling
    - `AuthenticationException`: New exception for authentication-related failures (token generation, refresh, validation)

## Release (2025-09-30)
- `core`: [v0.2.0](core/CHANGELOG.md#v020)
  - **Feature:** Support for passing custom OkHttpClient objects
    - `KeyFlowAuthenticator`: Add new constructors with an `OkHttpClientParam`
      - Marked constructors without `OkHttpClient` param as deprecated, use new constructors with `OkHttpClient` instead
      - `KeyFlowAuthenticator` implements `okhttp3.Authenticator` interface now
      - added method `KeyFlowAuthenticator.authenticate()`
    - Marked `KeyFlowInterceptor` class as deprecated, use `KeyFlowAuthenticator` instead
    - Marked `SetupAuth` constructors and methods `SetupAuth.init()` and `SetupAuth.getAuthHandler()` as deprecated
      - all other methods of `SetupAuth` are marked as `static` now, only these will remain in the future
- `iaas`: 
  - [v0.3.0](services/iaas/CHANGELOG.md#v030)
    - **Feature:** Add `createdAt` and `updatedAt` attributes to `SecurityGroupRule`, `BaseSecurityGroupRule`, `CreateSecurityGroupRulePayload` model classes
    - **Feature:** Add `description` attribute to `CreateNicPayload`, `NIC`, `UpdateNicPayload` model classes
    - **Feature:** New model class `ServerAgent`
    - **Feature:** Add `agent` attribute to `Server`, `CreateServerPayload` model classes
  - [v0.2.0](services/iaas/CHANGELOG.md#v020)
    - **Feature:** Support for passing custom OkHttpClient objects
      - `ApiClient`
        - Added constructors with `OkHttpClient` param (recommended for production use)
        - Use new `KeyFlowAuthenticator` `okhttp3.Authenticator` implementation instead of request interceptor for authentication
      - `DefaultApi`: Added constructors with `OkHttpClient` param (recommended for production use)
      - `IaasApi`: Added constructors with `OkHttpClient` param (recommended for production use)
- `resourcemanager`: [v0.2.0](services/resourcemanager/CHANGELOG.md#v020)
  - **Feature:** Support for passing custom OkHttpClient objects
    - `ApiClient`
      - Added constructors with `OkHttpClient` param (recommended for production use)
      - Use new `KeyFlowAuthenticator` `okhttp3.Authenticator` implementation instead of request interceptor for authentication
    - `DefaultApi`: Added constructors with `OkHttpClient` param (recommended for production use)
    - `ResourceManagerApi`: Added constructors with `OkHttpClient` param (recommended for production use)
- `examples`:
  - Add example how to use custom `OkHttpClient` object

## Release (2025-09-09)
- `core`: [v0.1.0](core/CHANGELOG.md#v010)
  - Initial onboarding of STACKIT Java SDK core lib
- `iaas`: [v0.1.0](services/iaas/CHANGELOG.md#v010)
  - Initial onboarding of STACKIT Java SDK for IaaS service
- `resourcemanger`: [v0.1.0](services/resourcemanager/CHANGELOG.md#v010)
  - Initial onboarding of STACKIT Java SDK for Resourcemanager service

