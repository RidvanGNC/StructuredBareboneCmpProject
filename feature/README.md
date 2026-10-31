# Feature registry

Features are compiled into the app. A `FeatureModule` describes one: id, version, the feature ids it
offers, the Koin modules it needs and `onStartup` / `onSignedIn` hooks. Nothing is downloaded or executed
at run time; the server can only change which compiled features the user may see.

- `FeatureVisibilityPolicy` decides visibility. The base template uses `AllFeaturesVisible`. A product
  with entitlements registers its own `FeatureVisibilityPolicy` in Koin; the optional `:packages:tenant`
  provides `EntitlementVisibilityPolicy`, which shows only features the server granted. A policy that
  depends on changing state also emits on `changes`, so the menu follows it.
- `FeatureRegistry` answers `isVisible`, `visibleFeatureIds` and `activeModules`, and runs the hooks.
- Pass the modules to `initKoin(features = …)`; their Koin modules are loaded with the base module.
- A feature screen is contributed as a `FeatureDestination` (see `:navigation`); it appears in the menu only
  while the policy shows its feature.

Visibility is an experience concern only. The backend must still authorize every operation.
