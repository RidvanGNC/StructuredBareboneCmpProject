# React application guide

`webApp` is a standalone TypeScript/React application. It shares OpenAPI contracts with KMP and
owns its own UI, routing, state, dependency injection choices, and HTTP integration.

## Decide before expanding it

1. Does the product need client routing, server-state caching, forms, a component system, or a
   global store? Add no library until there is a concrete need.
2. Which authentication method supplies credentials to `ApiClient`? Never place credentials in
   `VITE_*` configuration.
3. Is tenant support required? If yes, mount `src/packages/tenant/TenantProvider` only around the
   tenant-aware application tree and use the OpenAPI contract.

Run `npm ci`, `npm run build`, `npm audit`, and `npm audit --omit=dev` for every dependency change.
See [SECURITY.md](SECURITY.md) and [ARCHITECTURE.md](ARCHITECTURE.md).
