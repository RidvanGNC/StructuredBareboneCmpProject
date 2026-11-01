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

        // ProGuard stays off in the release build: it removed the Ktor service providers.
        buildTypes.release.proguard {
            isEnabled.set(false)
        }

        nativeDistributions {
            targetFormats(TargetFormat.Msi)
            packageName = brand["desktopPackageName"] ?: "StructuredBareboneCmpProject"
            packageVersion = desktopVersionName.get()
            brand["vendor"]?.let { vendor = it }
            windows {
                // Per-user install without a directory page, and a Start menu entry.
                perUserInstall = true
                dirChooser = false
                shortcut = true
                menu = true
                brand["upgradeUuid"]?.let { upgradeUuid = it }
                brand["desktopIcon"]?.let { iconFile.set(File(it)) }
            }
        }
    }
}

// The upgrade code ties every release of a product together; an installer without a brand-specific one
// would replace or conflict with other products, so the MSI build refuses to run without it.
val hasUpgradeUuid = brand["upgradeUuid"] != null
tasks.matching { it.name.startsWith("package") && it.name.endsWith("Msi") }.configureEach {
    val upgradeUuidPresent = hasUpgradeUuid
    doFirst {
        check(upgradeUuidPresent) {
            "MSI packaging needs the brand key upgradeUuid (see gradle/brand.properties.example)."
        }
    }
}
