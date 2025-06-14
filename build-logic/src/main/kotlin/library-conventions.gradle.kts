import gradle.kotlin.dsl.accessors._a313ba380b190e27ff4471c793b5aeae.android
import gradle.kotlin.dsl.accessors._a313ba380b190e27ff4471c793b5aeae.androidTestImplementation
import gradle.kotlin.dsl.accessors._a313ba380b190e27ff4471c793b5aeae.testImplementation

plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = libs.compileSdk

    defaultConfig {
        minSdk = libs.minSdk

        testInstrumentationRunner = libs.testInstrumentationRunner
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    sourceSets["main"].kotlin {
        srcDir("src/main/samples/kotlin")
    }

    setupCompileOptions(libs)
    setupKotlinJvmOptions(libs)
    setupTestOptions()
}

dependencies {
    testImplementation(libs.testUnit)
    androidTestImplementation(libs.testInstrumentation)
}