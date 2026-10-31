import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val appVersionName = providers.environmentVariable("APP_VERSION_NAME")
    .orElse(providers.gradleProperty("app.version.name"))
val appVersionCode = providers.environmentVariable("APP_VERSION_CODE")
    .orElse(providers.environmentVariable("GITHUB_RUN_NUMBER"))
    .orElse(providers.gradleProperty("app.version.code"))
    .map(String::toInt)

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
        applicationId = "org.structured_barebone.cmp_project"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = appVersionCode.get()
        versionName = appVersionName.get()
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
    implementation(libs.androidx.activity.compose)
    implementation(libs.koin.android)
    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
}
