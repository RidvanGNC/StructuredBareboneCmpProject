# Android entry point

`androidApp` is the Android application module: manifest, launcher resources, `Application` and
`Activity`. Shared UI and logic live in `:composeApp` and the modules it depends on.

Android Gradle Plugin 9 does not allow the application plugin and the Kotlin Multiplatform plugin in
one module. Kotlin Multiplatform modules therefore use `com.android.kotlin.multiplatform.library`
and configure Android inside `kotlin { android { ... } }`. A module that ships Android resources
(`src/androidMain/res`) must set `androidResources { enable = true }`.

Build: `gradlew.bat :androidApp:assembleDebug`.
