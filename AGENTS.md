# AI contributor instructions

This is a template, not a product with fixed business choices. Before an AI changes architecture,
authentication, tenancy, package delivery, persistence, routing, or a public API, it must inspect
the relevant decision guide and ask the user only the unresolved questions below. Do not infer a
high-impact answer from demo fixtures.

## Required decision gates

### Tenant or authorization work

Ask:

1. Is this deployment single-tenant, multi-tenant, or optionally tenant-aware?
2. What backend-owned identity, membership, role, and isolation model is authoritative?
3. Which clients need tenant selection, and which packages are entitlement-controlled?

Then read `docs/tenant-architecture.md`, `packages/tenant/README.md`, and
`contracts/tenant-api.openapi.yaml`. Treat every client-provided tenant ID as a selector, never as
authorization proof.

### New feature package work

Ask:

1. Is the package installed for every release, entitlement-controlled, or experimental?
2. Which KMP and React implementations are required?
3. What API contract, roles, audit events, and removal/migration path are required?

Register only compiled local code. Do not introduce remote code loading.

### Web work

Ask whether the user needs routing, server-state caching, forms, a UI system, or authentication
before adding libraries. Check maintainers, licenses, transitive dependencies, advisories, lockfile,
`npm audit`, and `npm audit --omit=dev` before accepting a dependency change.

### KMP work

Ask which targets are required and whether the feature needs native UI or shared Compose UI. Keep
platform-specific implementations behind source-set boundaries and verify every locally available
target.

## Working rules

- Prefer API contracts over shared KMP/React implementation code.
- Keep optional packages out of base-app dependencies until the host opts in.
- Keep demo data visibly labelled as fixture data.
- Update the nearest README/decision guide when a new choice or integration point is introduced.
- Never add secrets to client code, `VITE_*` values, fixtures, or documentation.
- Write all identifiers (variables, functions, classes, files) in English.
- Use stable releases only (no alpha, beta or RC); verify new dependency sets by building and running every target available locally.
- Android application code lives in `:androidApp`; Kotlin Multiplatform modules use `com.android.kotlin.multiplatform.library` (see `androidApp/README.md`).
- Run the tasks listed in `.github/workflows/ci.yml` before committing; the rule tests in `composeApp` check route, ViewModel and repository registration.
- Never hard-code user-visible text: use Compose Resources on KMP and `webApp/src/i18n` on the web; see the localization section of `docs/architecture-decisions.md` to add a language.
- Sizes, colors and type styles come from `:theme` tokens only; raw `dp`/`sp`, `Color(0x...)`, `fontSize`, `FontWeight` and `RoundedCornerShape` are allowed in `:theme` alone (`StyleRulesTest`, no exception list).
- Feature modules are compiled-in `FeatureModule`s in the `:feature` registry; entitlement filtering belongs to `:packages:tenant`, never to the base. Never add permission keys to the single-tenant base.

## Branding (removable)

`gradle/brand.gradle.kts` lets one build produce differently named apps: `-PbrandFile=<properties file>` (see `gradle/brand.properties.example`) or `-Pbrand.<key>=<value>` sets `productName`, `applicationId` and `desktopPackageName`. A product with a single fixed identity does not need it. To remove it, delete `gradle/brand.gradle.kts` and `gradle/brand.properties.example`; the builds then use the defaults written in `androidApp/build.gradle.kts` and `desktopApp/build.gradle.kts`, and the `brandValues` lines in the root `build.gradle.kts` can go as well. Do not read brand values anywhere else.

## Sign-in (removable)

Email and password sign-in is part of the template (`:screen:login`, the session gate in `composeApp/App.kt`, `/v1/auth/*` in `contracts/common.openapi.yaml`). It switches itself off when `AppConfig.apiBaseUrl` is empty. Ask the decision-gate questions before changing the authentication method; social sign-in and two-factor are not part of the template.
