# Release

`.github/workflows/release.yml` builds the Android APK and AAB and the Windows MSI, writes SHA-256 sums
and opens a **draft** GitHub release. A person reads the draft and publishes it. iOS and web are not part
of it.

The template does not say where an app is distributed or where it downloads updates from; the workflow
only produces files. Hosting, store upload and any update address belong to the product repository.

## How a product uses it

A product repository is created from the template and keeps its own brand file and secrets there.
It can run the workflow directly (tag or manual run) or call it from its own workflow:

```yaml
jobs:
  release:
    uses: ./.github/workflows/release.yml
    with:
      version: "1.4.0"
      brand_file: branding/product.properties
    secrets: inherit
```

| Trigger | Version name |
| --- | --- |
| Push of a tag `v1.2.3` (`v1.2.3-rc.1` is marked as a pre-release) | the tag without `v` |
| Manual run or `workflow_call` | the `version` input (SemVer, no `v`) |

The version code is the run number (`GITHUB_RUN_NUMBER`), as in `VERSIONING.md`. A version that is not
SemVer stops the run before anything is built. The CI workflow (`ci.yml`) runs first and must pass.

## Produced files

| File | From |
| --- | --- |
| `app-<version>.apk` | `:androidApp:assembleRelease` (R8 and resource shrinking on) |
| `app-<version>.aab` | `:androidApp:bundleRelease` |
| `app-<version>.msi` | `:desktopApp:packageReleaseMsi` (ProGuard off) |
| `SHA256SUMS.txt` | the three files above |
| `mapping-<version>.txt` | R8 mapping, needed to read release crash reports |

Network logging is off in release builds: it follows `AppLog.debug`, which Android sets only for a
debuggable app.

## Brand file

Pass the path of a properties file as `brand_file` (relative to the repository root); without it the
defaults in the application modules are used. Keys are listed in `gradle/brand.gradle.kts` and
`gradle/brand.properties.example`. The MSI needs `upgradeUuid`: generate one UUID per product and never
change it, or Windows treats later releases as a different product. The application id and the Android
signing key also belong together for the life of the app.

## Secrets and variables

Set these in the product repository (never in the template, never in files):

| Name | Kind | Purpose |
| --- | --- | --- |
| `ANDROID_KEYSTORE_BASE64` | secret | the release keystore, base64 encoded |
| `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD` | secret | its credentials |
| `APP_API_BASE_URL` | variable | backend root URL; empty builds the app without sign-in on fixture data |

Without the keystore the APK and AAB are built unsigned (the run prints a warning); an unsigned APK cannot
be installed. The MSI is not code-signed; Windows SmartScreen warns about unsigned installers until a
signing certificate is added to the workflow.

## Verified and not verified

Checked locally: the signed release APK and AAB (application id, version, label, signature, icons from the
brand directory); the release app on an Android 14 emulator against a mock backend (sign-in, wrong
password, profile, language change) with R8 on; the MSI properties (product name, version, publisher,
upgrade code, per-user, shortcuts); the packaged desktop app starting; the MSI refusing to build without
`upgradeUuid`. Not checked: the workflow itself on GitHub, installing the MSI, a Play Store upload, iOS.
