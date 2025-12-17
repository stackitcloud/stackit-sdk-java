## v0.3.1
- Bump dependency `cloud.stackit.sdk.core` to v0.4.1

## v0.3.0
- **Feature:** Add `createdAt` and `updatedAt` attributes to `SecurityGroupRule`, `BaseSecurityGroupRule`, `CreateSecurityGroupRulePayload` model classes
- **Feature:** Add `description` attribute to `CreateNicPayload`, `NIC`, `UpdateNicPayload` model classes
- **Feature:** New model class `ServerAgent`
- **Feature:** Add `agent` attribute to `Server`, `CreateServerPayload` model classes

## v0.2.0
- **Feature:** Support for passing custom OkHttpClient objects
  - `ApiClient`
    - Added constructors with `OkHttpClient` param (recommended for production use)
    - Use new `KeyFlowAuthenticator` `okhttp3.Authenticator` implementation instead of request interceptor for authentication
  - `DefaultApi`: Added constructors with `OkHttpClient` param (recommended for production use)
  - `IaasApi`: Added constructors with `OkHttpClient` param (recommended for production use)

## v0.1.0
- Initial onboarding of STACKIT Java SDK for IaaS service
