import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    //Serialization
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(), iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "navigation"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.androidx.lifecycle.viewmodelCompose)

            //Serialization
            implementation(libs.kotlinx.serialization.json)

            // Navigation
            implementation(libs.navigation.compose)

            // Coroutines for Flows and ViewModelScope
            implementation(libs.kotlinx.coroutines.core)

            // Project
            implementation(project(path = ":shared"))
            implementation(project(path = ":screen:mails"))
            implementation(project(path = ":screen:mail-detail"))
            implementation(project(path = ":screen:routing-contacts"))
            implementation(project(path = ":screen:pricing-rates"))
            implementation(project(path = ":screen:settings"))
            implementation(project(path = ":screen:user"))
            implementation(project(path = ":screen:mail-groups"))
        }
    }
}

android {
    namespace = "org.structured_barebone.navigation"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
