import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val appVersionName = providers.environmentVariable("APP_VERSION_NAME")
    .orElse(providers.gradleProperty("app.version.name"))
val appVersionCode = providers.environmentVariable("APP_VERSION_CODE")
    .orElse(providers.environmentVariable("GITHUB_RUN_NUMBER"))
    .orElse(providers.gradleProperty("app.version.code"))
    .map(String::toInt)

// Optional branding from gradle/brand.gradle.kts; empty when that file was removed.
@Suppress("UNCHECKED_CAST")
val brand = rootProject.extra["brandValues"] as Map<String, String>

// Backend root URL. Without it the app runs on fixture data and shows no sign-in.
val apiBaseUrl = providers.environmentVariable("APP_API_BASE_URL")
    .orElse(providers.gradleProperty("apiBaseUrl"))
    .orElse("")

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

android {
    namespace = "org.structured_barebone.cmp_project"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = brand["applicationId"] ?: "org.structured_barebone.cmp_project"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = appVersionCode.get()
        versionName = appVersionName.get()
        manifestPlaceholders["appLabel"] = brand["productName"] ?: "@string/app_name"
        buildConfigField("String", "API_BASE_URL", "\"${apiBaseUrl.get()}\"")
    }
    buildFeatures {
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":composeApp"))
    implementation(project(":di"))
    implementation(project(":data"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.koin.android)
    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
}
