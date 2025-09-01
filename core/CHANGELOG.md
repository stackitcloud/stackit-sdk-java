## v0.4.0
- **Feature:** Added core wait handler structure which can be used by every service waiter implementation.

## v0.3.0
- **Feature:** New exception types for better error handling
  - `AuthenticationException`: New exception for authentication-related failures (token generation, refresh, validation)

## v0.2.0
- **Feature:** Support for passing custom OkHttpClient objects
  - `KeyFlowAuthenticator`: Add new constructors with an `OkHttpClientParam`
    - Marked constructors without `OkHttpClient` param as deprecated, use new constructors with `OkHttpClient` instead
    - `KeyFlowAuthenticator` implements `okhttp3.Authenticator` interface now
      - added method `KeyFlowAuthenticator.authenticate()`
  - Marked `KeyFlowInterceptor` class as deprecated, use `KeyFlowAuthenticator` instead
  - Marked `SetupAuth` constructors and methods `SetupAuth.init()` and `SetupAuth.getAuthHandler()` as deprecated
    - all other methods of `SetupAuth` are marked as `static` now, only these will remain in the future

## v0.1.0
- Initial onboarding of STACKIT Java SDK core lib
