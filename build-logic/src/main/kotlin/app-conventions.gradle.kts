import gradle.kotlin.dsl.accessors._4a1608c7e4c1251e896c27efcc41b09e.android
import gradle.kotlin.dsl.accessors._4a1608c7e4c1251e896c27efcc41b09e.androidTestImplementation
import gradle.kotlin.dsl.accessors._4a1608c7e4c1251e896c27efcc41b09e.testImplementation

plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = libs.compileSdk

    defaultConfig {
        minSdk = libs.minSdk
        targetSdk = libs.targetSdk
        testInstrumentationRunner = libs.testInstrumentationRunner
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    setupCompileOptions(libs)
    setupKotlinJvmOptions(libs)
    setupTestOptions()
}

dependencies {
    testImplementation(libs.testUnit)
    androidTestImplementation(libs.testInstrumentation)
}