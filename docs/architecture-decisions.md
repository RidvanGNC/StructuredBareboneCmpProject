# Architecture decision guide

Answer these before adding a product feature. Record the result in the product repository or issue;
the template must not silently decide them for a consuming team.

1. Is this product single-tenant, multi-tenant, or a single-tenant product that may migrate later?
2. Which clients need the feature: CMP, React, or both? What is shared: contract, business rule, or
   only product language?
3. Is the feature always shipped, entitlement-controlled, or an experiment? Is it safe to remove?
4. What backend API version owns authorization and data isolation? Does an OpenAPI contract exist?
5. Which user roles, audit events, retention rules, and failure states are required?

Do not call a remote package manifest executable code. Client packages are compiled into releases;
the server only grants access to known features.

## Error model

Failed API responses are problem documents (RFC 9457) with a stable `code` and a `traceId`; successful
responses are unwrapped. Below the presentation layer an error is data only (`AppError`: kind, code,
status, trace id, field errors) and never carries user-visible text, so changing the language does not
leave stale text in state. The UI maps kinds and codes to localized text. See `contracts/README.md`.

## Session and secrets

Tokens are stored only in the platform `SecureKeyStore`; `Settings` (plain text) holds just the
"a session exists" flag. An entry that cannot be decrypted ends the session instead of failing, and
the user signs in again. Sign-out goes through `SessionCleaner`: product steps first (time-limited,
best effort), tokens last and always. A refresh token the server rejects ends the session the same way,
and the app shell observes `TokenProvider.isLoggedIn`. Details and platform status: `data/README.md`.

## App shell and features

The shell (`composeApp/App.kt`) decides what the user sees: the update screen when the server answered 426,
a loading state while a stored session is checked, the sign-in screen while signed out, otherwise the
navigation. Sign-in is email and password (`contracts/common.openapi.yaml`); without a configured API root
(`AppConfig`) there is no sign-in and the screens show fixture data. A session that ends by itself returns the
user to sign-in with a message; a sign-out chosen by the user does not show one.

Features are compiled-in `FeatureModule`s in the `:feature` registry. Which of them the user sees is a
`FeatureVisibilityPolicy`: everything by default, the server's entitlements when `:packages:tenant` is added.
Visibility only shapes the experience; the backend authorizes every operation. The base has no permission
keys. Theme and layout: sizes, colors and type styles come from `:theme` tokens, and the layout switches
between a bottom bar (phones) and a side rail (wide windows).

## Localization

User-visible text is never written in code. On KMP it lives in Compose Resources
(`composeResources/values/strings.xml` for English, `values-<tag>/strings.xml` for each other language,
next to the module that uses it); on the web it lives in the typed dictionaries in `webApp/src/i18n`.
Only values that do not change with the language (keys, ids, header names, timeouts) are `const val`.

- The language choice (`AppLanguage`: system default, English, Turkish) is stored in the device
  preferences. Changing it sets the process locale and rebuilds the root composition (`key(language)`), so
  nothing keeps text in the previous language and the app does not restart. State never holds formatted
  text: errors are `AppError` data and the UI maps them to resources.
- To add a language: add an `AppLanguage` entry, add a `values-<tag>/strings.xml` next to every
  `values/strings.xml`, add the web dictionary and its name in every dictionary. The tests in
  `composeApp/src/jvmTest/.../LocalizationRulesTest.kt` fail when a file, key or placeholder is missing or
  when a composable contains a string literal; the web compiler checks the dictionaries.
- Android and desktop read the process locale (`Locale.getDefault()`) when resources are resolved. iOS
  reads the app language at process start, so an iOS change needs a restart (unverified, no Mac available).
