# Dependency injection

The base Koin module intentionally wires only base-template dependencies. Optional capabilities
such as `:packages:tenant` must be registered by a consuming host during its own Koin initialization.

Do not turn an optional package into a base dependency merely to simplify a demo. Decide the host's
tenant mode and feature-package requirements first, then add the relevant module explicitly.

`platformModule` provides the preferences store (`Settings`: Android SharedPreferences, JVM
`java.util.prefs`, iOS `NSUserDefaults`) and the `SecureKeyStore` (Android Keystore, Windows DPAPI,
iOS Keychain); the iOS actuals are unverified. Preferences are plain text on every platform: never keep
tokens, keys or other secrets there.

The common module registers `TokenProvider` (`TokenStorage`) and `SessionCleaner`, which receives every
`SignOutStep` registered in Koin.
