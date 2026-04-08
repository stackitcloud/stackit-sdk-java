## v1.1.0
- **Feature:** Add `cascade` parameter to `deleteVolume` methods in `DefaultApi` / `IaasApi` class
- **Feature:** Add methods for new attribute `configDrive` in `CreateServerPayload` and `Server` class

## v1.0.1
- **Docs:** Extend docs of class `PortRange`

## v1.0.0
- **Breaking Change:** Add required `region` parameter to most API methods in `DefaultApi` class.
- **Breaking Change:** Removal of API methods from `DefaultApi` class: `listSnapshots`, `updateImageScopeLocal`, `updateImageScopePublic`.
- **Feature:** Add new API methods to `DefaultApi` class:
  - `addRoutesToRoutingTable`
  - `addRoutingTableToAreaPayload`
  - `createNetworkAreaRegion`
  - `deleteNetworkAreaRegion`
  - `deleteRouteFromRoutingTable`
  - `deleteRoutingTableFromArea`
  - `getNetworkAreaRegion`
  - `getRouteOfRoutingTable`
  - `getRoutingTableOfArea`
  - `imageFromVolume`
  - `listNetworkAreaRegions`
  - `listRoutesOfRoutingTable`
  - `listRoutingTablesOfArea`
  - `listSnapshotsInProject`
  - `updateNetworkAreaRegion`
  - `updateRouteOfRoutingTable`
  - `updateRoutingTableOfArea`
- Update of regex validators for model class attributes
  - Update regex validators for `ip` attribute in `PublicIp`, `UpdatePublicIPPayload`, `CreatePublicIPPayload` model classes
  - Update regex validators for `gateway` attribute in `UpdateNetworkIPv4Body`, `UpdateNetworkIPv6Body` model classes
- **Feature:** New model classes
  - Network area:
    - `RegionalArea`, `RegionalAreaListResponse`
    - `CreateNetworkAreaRegionPayload`
    - `RegionalAreaIPv4`, `UpdateRegionalAreaIPv4`
  - Routing tables:
    - `RoutingTable`, `RoutingTableListResponse`
    - `AddRoutesToRoutingTablePayload`
    - `AddRoutingTableToAreaPayload`
    - `UpdateRouteOfRoutingTablePayload`, `UpdateRoutingTableOfAreaPayload`
  - Routes:
    - `RouteDestination`, `DestinationCIDRv4`, `DestinationCIDRv6`
    - `RouteNexthop`, `NexthopInternet`, `NexthopIPv4`, `NexthopIPv6`, `NexthopBlackhole`
  - Network (IPv4): `NetworkIPv4`, `CreateNetworkIPv4`, `CreateNetworkIPv4WithPrefix`, `CreateNetworkIPv4WithPrefixLength`
  - Network (IPv6): `NetworkIPv6`, `CreateNetworkIPv6`, `CreateNetworkIPv6WithPrefix`, `CreateNetworkIPv6WithPrefixLength`
  - other: `CreateServerPayloadAllOfNetworking`, `ImageFromVolumePayload`, `UpdateNetworkAreaRegionPayload`, `ServerNetworking`
- **Feature:** New attributes in model classes
  - Add `region` attribute to `PublicNetwork` model class
  - Add `destination` attribute to `Route` model class
  - Add `importProgress` attribute to model classes `CreateImagePayload`, `Image`
  - Add `encrypted` attribute to model class `Backup`
  - Add `ipv4`, `ipv6`, `routingTableId` attributes to model class `CreateNetworkPayload`, `PartialUpdateNetworkPayload`
  - Add `ipv4`, `ipv6` `routingTableId` attributes to model class `Network`
  - Add `items` attribute to `CreateNetworkAreaRoutePayload` model class
- **Breaking Change:**: Removal of model classes
  - Network area: `Area`, `AreaConfig`, `CreateAreaAddressFamily`, `UpdateAreaAddressFamily`, `AreaPrefixConfigIPv4`, `CreateAreaIPv4`, `UpdateAreaIPv4`
  - Server: `CreateServerPayloadNetworking`
  - Network: `CreateNetworkIPv4Body`, `NetworkAreaIPv4`, `CreateNetworkAddressFamily`, `UpdateNetworkAddressFamily`, `CreateNetworkIPv6Body`
- **Breaking Change:** Renaming of ID attributes in model classes
  - Renaming of attribute `networkRangeId` to `id` in `NetworkRange` model class
  - Renaming of attribute `routeId` to `id` in `Route` model class
  - Renaming of attribute `networkId` to `id` in `Network` model class
  - Renaming of attribute `areaId` to `id` in `NetworkArea` model class
  - Renaming of attribute `projectId` to `id` in `Project` model class
- **Breaking Change:** Renaming of `state` attribute to `status` in model classes `Network`, `NetworkArea`, `Project`
- **Breaking Change:** Type changes of attributes of model classes
  - Change type of `networking` attribute from `CreateServerPayloadNetworking` to `ServerNetworking` in `Server` model class
  - Change type of `networking` attribute from `CreateServerPayloadNetworking` to `CreateServerPayloadAllOfNetworking` in `CreateServerPayload` model class
  - Change type of `nexthop` attribute from string to `RouteNexthop` in `Route` model class
- **Breaking Change:**
  - Remove attribute `prefix` from `Route` model class
  - Remove attribute `ipv4` from `NetworkArea`, `CreateNetworkAreaRoutePayload` model classes
  - Remove attribute `address_family` from `CreateNetworkAreaPayload`, `CreateNetworkPayload`, `PartialUpdateNetworkAreaPayload`, `PartialUpdateNetworkPayload` model classes
  - Remove attributes `gateway`, `gatewayv6`, `nameservers`, `nameserversV6`, `prefixes`, `prefixesV6`, `publicIp` from `Network` model class
  - Remove attribute `openstack_project_id` from `Project` model class
- **Feature:** Add `CreateIsolatedNetwork` functionality
- **Feature:** Add `ImageFromVolumePayload` functionality
- **Feature:** Add `SystemRoutes` to `UpdateRoutingTableOfAreaPayload`
- **Improvement:** Support additionalProperties in models
  - This allows accessing new properties, even if they aren't yet defined in the model


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
