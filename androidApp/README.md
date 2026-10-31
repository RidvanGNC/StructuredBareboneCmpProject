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
