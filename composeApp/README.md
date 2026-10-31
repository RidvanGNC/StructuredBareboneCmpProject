# CMP application guide

`composeApp` is the shared Compose library (Android, JVM, iOS). `androidApp` owns the Android
application, `desktopApp` owns the desktop window and `iosApp` owns the native iOS wrapper. The Operations Workspace screen is fixture content only.

`App()` is the app shell: theme and language (changing the language rebuilds the UI but keeps the
current screen), the update gate (HTTP 426 blocks the app), the sign-in gate and the message strip, around
the navigation. Pass `featureDestinations` for compiled-in feature screens. The tests in `src/jvmTest`
check the structure (`ModuleRulesTest`), the localization and style rules, and drive the shell with a
fake backend (`AppFlowTest`, which also writes screenshots to `build/ui-shots`).

Before adding a feature, decide its target set, whether the UI is shared Compose or native, its API
contract, and whether it belongs to an optional package. Keep `:packages:tenant` opt-in unless the
host product has explicitly selected tenant-aware operation.
