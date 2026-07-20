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
        // MSAL Android'in "display-mask" (Surface Duo) transitive bagimliligi icin gerekli.
        maven {
            url = uri("https://pkgs.dev.azure.com/MicrosoftDeviceSDK/DuoSDK-Public/_packaging/Duo-SDK-Feed/maven/v1")
            content {
                includeGroup("com.microsoft.device.display")
            }
        }
    }
}

include(":composeApp")
include(":shared")
include(":navigation")
include(":viewmodel")
include(":di")

//Screens
include(":data")
include(":screen:mails")
include(":screen:user")
include(":screen:mail-detail")
include(":screen:routing-contacts")
include(":screen:pricing-rates")
include(":screen:settings")
include(":screen:mail-groups")
