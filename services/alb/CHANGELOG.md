## v0.3.0
- **Feature:** Add fields `usedCredentials` and `usedLoadbalancers` to `GetQuotaResponse`
- **Improvement:** Support additional properties in models

## v0.2.0
- **Feature:** Switch from `v2beta` API version to `v2` version.
- **Feature:** `MaxCredentials` field added to `GetQuotaResponse`
- **Breaking change:** added `version` to LoadBalancer constructor
- **Breaking change:** renamed `exact` to `exactMatch` in Path model
- **Breaking change:** removed `pathPrefix` from Rule model

## v0.1.1
- Bump dependency `cloud.stackit.sdk.core` to v0.4.1

## v0.1.0
- Initial onboarding of STACKIT Java SDK for Application load balancer service