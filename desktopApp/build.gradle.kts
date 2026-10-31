import org.jetbrains.compose.desktop.application.dsl.TargetFormat

val desktopVersionName = providers.environmentVariable("APP_VERSION_NAME")
    .orElse(providers.gradleProperty("app.version.name"))
    .map { it.substringBefore("-") }

// Optional branding from gradle/brand.gradle.kts; empty when that file was removed.
@Suppress("UNCHECKED_CAST")
val brand = rootProject.extra["brandValues"] as Map<String, String>

// Backend root URL. Without it the app runs on fixture data and shows no sign-in.
val apiBaseUrl = providers.environmentVariable("APP_API_BASE_URL")
    .orElse(providers.gradleProperty("apiBaseUrl"))
    .orElse("")

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    sourceSets {
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            // Provides Dispatchers.Main on the JVM (viewModelScope); without it the desktop app crashes on start.
            implementation(libs.kotlinx.coroutines.swing)
            implementation(project(":composeApp"))
            implementation(project(":di"))
            implementation(project(":data"))
        }
    }
}

compose.desktop {
    application {
        mainClass = "org.structured_barebone.desktop.MainKt"
        // The server reads the app version from the X-Client-Version header (see AppInfo).
        jvmArgs += "-Dapp.version=${desktopVersionName.get()}"
        jvmArgs += "-Dapp.apiBaseUrl=${apiBaseUrl.get()}"

        nativeDistributions {
            targetFormats(TargetFormat.Msi)
            packageName = brand["desktopPackageName"] ?: "StructuredBareboneCmpProject"
            packageVersion = desktopVersionName.get()
        }
    }
}
