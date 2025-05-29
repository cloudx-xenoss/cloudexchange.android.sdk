import gradle.kotlin.dsl.accessors._a313ba380b190e27ff4471c793b5aeae.implementation

plugins {
    id("library-conventions")
    id("cloudx-sdk-publishing-conventions")
}

android {
    defaultConfig {
        consumerProguardFile("../build-logic/common-adapter-consumer-rules.pro")
    }
}

dependencies {
    implementation(project(":sdk"))
}