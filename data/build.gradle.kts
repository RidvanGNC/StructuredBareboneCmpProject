import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    jvm {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
    }

    android {
        namespace = "org.structured_barebone.data"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
    }
    listOf(iosArm64(), iosSimulatorArm64())

    sourceSets {
        androidMain.dependencies {
            implementation(libs.ktor.client.android)
        }
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.auth)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.multiplatform.settings)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(project(":shared"))
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.cio)
            implementation(libs.jna.platform)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
            implementation(libs.multiplatform.settings.test)
        }
    }
}
