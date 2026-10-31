import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidKotlinMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
}

// Optional branding (see AGENTS.md): without gradle/brand.gradle.kts the modules fall back to their defaults.
extra["brandValues"] = emptyMap<String, String>()
rootProject.file("gradle/brand.gradle.kts").takeIf { it.isFile }?.let { apply(from = it) }

subprojects {
    plugins.withId("org.jetbrains.kotlin.plugin.compose") {
        extensions.configure<ComposeCompilerGradlePluginExtension> {
            stabilityConfigurationFiles.add(rootProject.layout.projectDirectory.file("compose-stability.conf"))
            if (providers.gradleProperty("composeMetrics").isPresent) {
                val metricsDirectory = layout.buildDirectory.dir("compose-metrics")
                metricsDestination.set(metricsDirectory)
                reportsDestination.set(metricsDirectory)
            }
        }
    }
}
