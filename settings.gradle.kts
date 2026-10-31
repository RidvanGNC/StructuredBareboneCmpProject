rootProject.name = "StructuredBareboneCmpProject"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":composeApp")
include(":androidApp")
include(":desktopApp")
include(":shared")
include(":navigation")
include(":viewmodel")
include(":di")
include(":theme")
include(":ui")
include(":feature")

// Optional product capabilities. Apps opt in explicitly; the base template has no tenancy dependency.
include(":packages:tenant")

//Screens
include(":data")
include(":screen:template")
include(":screen:login")
include(":screen:profile")
