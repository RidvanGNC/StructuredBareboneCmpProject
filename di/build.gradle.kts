import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
}

kotlin {
    jvm {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
    }

    android {
        namespace = "org.structured_barebone.di"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
    }
    listOf(iosArm64(), iosSimulatorArm64())

    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.multiplatform.settings)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.ktor.client.core)
            implementation(project(":data"))
            implementation(project(":feature"))
            implementation(project(":shared"))
            implementation(project(":viewmodel"))
        }
    }
}
