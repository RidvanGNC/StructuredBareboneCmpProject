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
        jniLibs {
            // Prebuilt library from Compose; where the NDK strip tool is missing (CI runners) AGP warns that it
            // cannot strip it and packages it unchanged, which is what this setting does without the warning.
            keepDebugSymbols += "**/libandroidx.graphics.path.so"
        }
    }
    // Release signing comes from the environment (CI secrets) or from -P properties; never from files in the
    // repository. Without a keystore the release build is produced unsigned.
    val keystoreFile = providers.environmentVariable("ANDROID_KEYSTORE_FILE")
        .orElse(providers.gradleProperty("android.keystore.file"))
    signingConfigs {
        if (keystoreFile.isPresent) {
            create("release") {
                storeFile = file(keystoreFile.get())
                storePassword = providers.environmentVariable("ANDROID_KEYSTORE_PASSWORD")
                    .orElse(providers.gradleProperty("android.keystore.password")).get()
                keyAlias = providers.environmentVariable("ANDROID_KEY_ALIAS")
                    .orElse(providers.gradleProperty("android.key.alias")).get()
                keyPassword = providers.environmentVariable("ANDROID_KEY_PASSWORD")
                    .orElse(providers.gradleProperty("android.key.password")).get()
            }
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfigs.findByName("release")?.let { signingConfig = it }
        }
    }
    // A product's launcher icons (brand key androidIconDir) replace the template ones.
    brand["androidIconDir"]?.let { iconDir ->
        sourceSets {
            getByName("debug").res.directories.add(iconDir)
            getByName("release").res.directories.add(iconDir)
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
