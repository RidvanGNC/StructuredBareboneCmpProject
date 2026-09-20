# Optional tenant and package architecture

The base template remains single-tenant and does not depend on `:packages:tenant` or the React
tenant package. Tenant-aware products opt in at composition time; removal is therefore a dependency
and integration change, not a search-and-replace across every feature.

## Trust boundary

The client sends a tenant ID only when requesting a context switch. It is a selector, not proof of
membership or authority. The backend must authenticate the subject, validate active membership,
derive the effective tenant context, apply authorization and isolate tenant-owned data. Clients use
the returned context only for navigation and experience; hiding UI never authorizes an operation.

`contracts/tenant-api.openapi.yaml` is the source contract for this boundary. It deliberately
returns a fresh `authorizationVersion` after every context selection so the client can discard stale
entitlements. Token acquisition, refresh and storage belong to the host authentication integration,
not this package.

## Feature packages

A feature package is compiled into each client release and declares its local package ID, version,
and feature IDs. The feature catalog intersects that local registry with server entitlements. A
server response can enable or disable known code; it cannot make an arbitrary remote module execute.
This avoids remote-code loading and keeps mobile-store delivery rules intact.

## Integration

### KMP

Add `implementation(project(":packages:tenant"))` only in a tenant-aware app, then register the
module while initializing Koin:

```kotlin
initKoin {
    modules(tenantPackageModule(TenantPackageConfig(apiBaseUrl = "https://api.example.com")))
}
```

Presentation code obtains `TenantSessionStore`, launches `refresh()` in its lifecycle-owned scope,
and renders its `StateFlow`. It must not create a long-lived scope in the package itself.

### React

Create `HttpTenantRepository` with the host's authenticated `ApiClient`, then mount
`TenantProvider` around only the tenant-aware route tree. The base SPA remains independent until it
explicitly imports that package.

## Backend verification checklist

- Authorize every tenant-scoped API and data access using the server-verified subject and active
  membership, including background jobs and cache reads.
- Scope cache, storage, rate limits and audit events by verified tenant context where applicable.
- Make cross-tenant administration explicit, separately authorized and auditable.
- Add negative authorization tests proving a member of tenant A cannot read or mutate tenant B data.
