import org.jetbrains.compose.desktop.application.dsl.TargetFormat

val desktopVersionName = providers.environmentVariable("APP_VERSION_NAME")
    .orElse(providers.gradleProperty("app.version.name"))
    .map { it.substringBefore("-") }

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

        nativeDistributions {
            targetFormats(TargetFormat.Msi)
            packageName = "StructuredBareboneCmpProject"
            packageVersion = desktopVersionName.get()
        }
    }
}
