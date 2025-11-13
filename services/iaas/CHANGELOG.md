## v1.0.0
- **Breaking Change:** The region must be passed as a parameter to any region-specific request.
- **Feature:** Add new methods to manage routing tables: `addRoutingTableToArea`, `deleteRoutingTableFromArea`, `getRoutingTableOfArea`, `listRoutingTablesOfArea`, `updateRoutingTableOfArea`
- **Feature:** Add new methods to manage routes in routing tables: `addRoutesToRoutingTable`, `deleteRouteFromRoutingTable`, `getRouteOfRoutingTable`, `listRoutesOfRoutingTable`, `updateRouteOfRoutingTable`
- **Breaking Change:** Add new method to manage network area regions: `createNetworkAreaRegion`, `deleteNetworkAreaRegion`, `getNetworkAreaRegion`, `listNetworkAreaRegions`, `updateNetworkAreaRegion`
- **Feature:** Add new field `Encrypted` to `Backup` model, which indicates if a backup is encrypted
- **Feature:** Add new field `ImportProgress` to `Image` model, which indicates the import progress of an image
- **Breaking Change:** Remove field `addressFamily` in `CreateNetworkAreaPayload` model
- **Breaking Change:** `Network` model has changed:
  - Rename `networkId` to `id`
  - Rename `state` to `status`
  - Move fields `gateway`, `nameservers`, `prefixes` and `publicIp` to new model `NetworkIPv4`, and can be accessed in the new field `ipv4`
  - Move fields `gatewayv6`, `nameserversv6` and `prefixesv6` to new model `NetworkIPv6`, and can be accessed in the new field `ipv6`
  - Add new field `routingTabledId` 
- **Breaking Change:** `NetworkArea` model has changed:
  - Rename `areaId` to `id`
  - Remove field `ipv4`
- **Breaking Change:** Rename `networkRangeId` to `id` in `NetworkRange` model
- **Breaking Change:** `CreateServerPayload` model has changed:
  - Model `CreateServerPayloadBootVolume` of `BootVolume` property changed to `ServerBootVolume`
  - Property `Networking` in `CreateServerPayload` is required now

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
