# CMP application guide

`composeApp` is the shared Compose library (Android, JVM, iOS). `androidApp` owns the Android
application, `desktopApp` owns the desktop window and `iosApp` owns the native iOS wrapper. The Operations Workspace screen is fixture content only.

Before adding a feature, decide its target set, whether the UI is shared Compose or native, its API
contract, and whether it belongs to an optional package. Keep `:packages:tenant` opt-in unless the
host product has explicitly selected tenant-aware operation.
