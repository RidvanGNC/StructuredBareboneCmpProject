# Android entry point

`androidApp` is the Android application module: manifest, launcher resources, `Application` and
`Activity`. Shared UI and logic live in `:composeApp` and the modules it depends on.

Android Gradle Plugin 9 does not allow the application plugin and the Kotlin Multiplatform plugin in
one module. Kotlin Multiplatform modules therefore use `com.android.kotlin.multiplatform.library`
and configure Android inside `kotlin { android { ... } }`. A module that ships Android resources
(`src/androidMain/res`) must set `androidResources { enable = true }`.

Build: `gradlew.bat :androidApp:assembleDebug`.

## Configuration

- Backend: `-PapiBaseUrl=https://...` or the `APP_API_BASE_URL` environment variable sets the API root
  (`BuildConfig.API_BASE_URL`). Without it the app runs on fixture data and shows no sign-in.
- Branding (optional): see "Branding" in `AGENTS.md`. `productName` becomes the launcher label and
  `applicationId` the application id.
- The encrypted token file (`secure_store`) is excluded from backups (`res/xml`), because its key never
  leaves the device.

## Release build

`gradlew.bat :androidApp:assembleRelease :androidApp:bundleRelease` produces the APK and the AAB. The
release build runs R8 with resource shrinking (`proguard-rules.pro`; add a rule only for a failure a
release run actually showed) and writes the mapping file to `build/outputs/mapping/release/mapping.txt`;
keep it, because it is the only way to read a release crash report.

Signing comes from the environment, never from the repository: `ANDROID_KEYSTORE_FILE`,
`ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD` (or the Gradle properties
`android.keystore.file`, `android.keystore.password`, `android.key.alias`, `android.key.password`).
Without a keystore the build is unsigned. The signing key and the application id belong together: a
different key cannot update an installed app. The whole flow is in `docs/release.md`.

A product's own launcher icons go in a directory of res-style folders (`mipmap-*/ic_launcher*`) named by
the brand key `androidIconDir`; they replace the template icons.
