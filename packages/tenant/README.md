# Optional tenant package

`:packages:tenant` is a KMP capability package, not a base-app dependency. It contains tenant
context models, an API adapter, a state store, and `EntitlementVisibilityPolicy`, the entitlement filter of the base
feature registry (`:feature`): it shows only compiled-in features the server enabled.

## Decide before integrating

1. Is multi-tenancy required now, optional, or explicitly out of scope?
2. Which backend authorization model verifies membership and tenant selection?
3. Which local package IDs and feature IDs are entitlement-controlled?
4. Which audit, cache, background-job, and data-isolation guarantees apply?

Integrate only after these answers are documented. `tenantPackageModule` also registers that policy as the `FeatureVisibilityPolicy`, so the base registry hides
features the server did not enable. Add `tenantPackageModule` during the host's Koin
initialization; do not add this module to the template's default `composeApp` dependency graph.

See `../../docs/tenant-architecture.md` for the trust boundary and backend checklist.
