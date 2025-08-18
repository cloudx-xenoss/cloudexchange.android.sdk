pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven(url = uri("https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea"))
    }
}

rootProject.name = "cloudx-sdk"

include(":sdk")
include(":demo")
include(":adapters:google")
include(":adapters:meta")
include(":adapters:mintegral")
include(":adapters:testbidder")
include(":adapters:cloudx")