# Structured Barebone CMP Project

This template has three independent client entry points: Android/iOS/desktop through Kotlin
Multiplatform and Compose, plus a standalone React web application. They share backend contracts,
not UI, navigation, dependency injection, or client data implementations.

## Quick start

```bash
# Android
gradlew.bat :androidApp:assembleDebug

# Desktop / Windows
gradlew.bat :desktopApp:run

# React web
cd webApp
npm ci
npm run dev
```

Use JDK 17+ for Gradle. React requires Node `^20.19.0 || >=22.12.0`.

## Demo

Both clients show an **Operations Workspace** fixture: packages, workspace metrics, and activity.
The labels are explanatory sample content, not a live tenancy implementation. See the decision
guides before connecting a backend.

## Key decisions

| Decision | Default | Read before changing |
| --- | --- | --- |
| Tenant mode | Single-tenant base; multi-tenant is opt-in | [tenant architecture](../tenant-architecture.md) |
| Web architecture | Independent React application | [webApp guide](../../webApp/README.md) |
| Feature delivery | Locally compiled packages enabled by server entitlements | [tenant package guide](../../packages/tenant/README.md) |
| Shared boundary | OpenAPI contract, not shared KMP/React implementation | [contracts guide](../../contracts/README.md) |

## Versioning and verification

See [VERSIONING.md](../../VERSIONING.md). Before committing, run the relevant Gradle compile/test
tasks, `npm run build`, and both npm audit commands for web changes.
