## v0.3.1
- Deprecate `getSchedule` and `listSchedules` methods in `SfsApi` class

## v0.3.0
- **Feature:** Add `disableLock`, `enableLock`, `getLock`, `getSchedule`, `listSchedules`, `getSnapshotPolicy`, `listSnapshotPolicies`,  methods to `DefaultApi` / `SfsApi` class
- **Feature:** New model classes: `EnableLockResponse`, `GetLockResponse`, `GetScheduleResponse`, `GetSnapshotPolicyResponse`, `ListSchedulesResponse`, `ListSnapshotPoliciesResponse`, `ResourcePoolSnapshotPolicy`, `Schedule`, `SnapshotPolicy`, `SnapshotPolicySchedule`, `UpdateResourcePoolSnapshotPayload`, `UpdateResourcePoolSnapshotResponse`
- **Feature:** Add methods for new attribute `snapshotPolicyId` in model class `UpdateResourcePoolPayload` and `CreateResourcePoolPayload`
- **Feature:** Add methods for new attribute `usedBySnapshotsGigabytes` in model class `ResourcePoolSpace`
- **Feature:** Add methods for new attribute `snaplockExpiryTime` in model class `ResourcePoolSnapshot`
- **Feature:** Add methods for new attribute `snapshotPolicy` in model class `ResourcePool`
- **Feature:** Add methods for new attribute `snaplockRetentionHours` in model class `CreateResourcePoolSnapshotPayload`

## v0.2.0
- **Breaking change:** The `name` and `spaceHardLimitGigabytes` fields are now marked as required for `ShareExportPayload`, `SharePayload`.

## v0.1.0
- Initial onboarding of STACKIT Java SDK for STACKIT File Storage (SFS) service
