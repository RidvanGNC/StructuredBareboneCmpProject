import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    android {
        namespace = "org.structured_barebone.app"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
        androidResources { enable = true }
    }

    listOf(
        iosArm64(), iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.navigation.compose)
            //Project
            implementation(project(path = ":navigation"))
            implementation(project(path = ":di"))
            implementation(project(path = ":data"))
            implementation(project(path = ":feature"))
            implementation(project(path = ":shared"))
            implementation(project(path = ":theme"))
            implementation(project(path = ":ui"))
            implementation(project(path = ":viewmodel"))
            implementation(project(path = ":screen:login"))
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmTest.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.compose.uiTest)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(project(":viewmodel"))
        }
    }
}

compose.resources {
    packageOfResClass = "org.structured_barebone.cmp_project.resources"
}
