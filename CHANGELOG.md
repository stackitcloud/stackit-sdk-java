## Release (2025-MM-DD)
- `core`: [v0.4.1](core/CHANGELOG.md/#v041)
  - **Bugfix:** Add check in `KeyFlowAuthenticator` to prevent endless loops
- `loadbalancer`: [v0.1.0](services/loadbalancer/CHANGELOG.md#v010)
  - Initial onboarding of STACKIT Java SDK for Load balancer service
- `alb`: [v0.1.0](services/alb/CHANGELOG.md#v010)
  - Initial onboarding of STACKIT Java SDK for Application load balancer service
- `objectstorage`: [v0.1.0](services/objectstorage/CHANGELOG.md#v010)
  - Initial onboarding of STACKIT Java SDK for Object storage service
- `serverupdate`: [v0.1.0](services/serverupdate/CHANGELOG.md#v010)
  - Initial onboarding of STACKIT Java SDK for Server Update service

## Release (2025-10-29)
- `core`:
  - [v0.4.0](core/CHANGELOG.md#v040)
    - **Feature:** Added core wait handler structure which can be used by every service waiter implementation.
  - [v0.3.0](core/CHANGELOG.md#v030)
    - **Feature:** New exception types for better error handling
      - `AuthenticationException`: New exception for authentication-related failures (token generation, refresh, validation)
- `resourcemanager`: 
  - [v0.4.0](services/resourcemanager/CHANGELOG.md#v040)
    - **Feature:** Added waiter for project creation and project deletion
  - [v0.3.0](services/resourcemanager/CHANGELOG.md#v030)
    - **Feature:** Add `ContainerSearchResult` model class for container search functionality


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

