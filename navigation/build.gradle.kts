import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    jvm {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
    }

    android {
        namespace = "org.structured_barebone.navigation"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
        androidResources { enable = true }
    }
    listOf(iosArm64(), iosSimulatorArm64())

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.serialization.json)
            implementation(project(":feature"))
            implementation(project(":theme"))
            implementation(project(":screen:template"))
            implementation(project(":screen:profile"))
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

compose.resources {
    packageOfResClass = "org.structured_barebone.navigation.resources"
}
