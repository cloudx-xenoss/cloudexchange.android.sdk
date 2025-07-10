plugins {
    id("library-conventions")
    id("dokka-conventions")
    alias(libs.plugins.ksp)
    jacoco
    id("cloudx-sdk-publishing-conventions")
}

android {
    namespace = libs.versions.sdkPackageName.get()

    defaultConfig {
        buildConfigField("String", "SDK_VERSION_NAME", "\"${libs.versions.sdkVersionName.get()}\"")
        buildConfigField("long", "SDK_BUILD_TIMESTAMP", "${System.currentTimeMillis()}")

        val configEndpoint = property("cloudx.endpoint.config")
        buildConfigField("String", "CLOUDX_ENDPOINT_CONFIG", """"$configEndpoint"""")
    }

    buildTypes {
        debug {
            enableAndroidTestCoverage = true
            enableUnitTestCoverage = true
        }
    }
}

dependencies {
    implementation(files("libs/XorEnc-1.0.1-obf.jar"))
    implementation(libs.kotlin.reflect)
    implementation(libs.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.ktor.client.android)
    implementation(libs.appcompat)
    implementation(libs.google.advertisingid)
    implementation(libs.google.location)

    implementation(libs.lifecycle.process)
    implementation(libs.lifecycle.runtime)

    implementation(libs.startup)

    // Room.
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)
}

tasks.withType(Test::class) {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.*", "sun.*")
    }
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

// Register a JacocoReport task for code coverage analysis
tasks.register<JacocoReport>("jacocoDebugCodeCoverage") {
    val exclusions = listOf(
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "**/Logger*.class",
    )

    val unitTests = "testDebugUnitTest"
    // TODO: add Android Tests to coverage measurements
    // val androidTests = "connectedDebugAndroidTest"
    // dependsOn(listOf(unitTests, androidTests))
    dependsOn(listOf(unitTests))
    group = "Reporting"
    description = "Execute unit tests, generate and combine Jacoco coverage report"
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    sourceDirectories.setFrom(layout.projectDirectory.dir("src/main/java"))
    classDirectories.setFrom(files(
        fileTree(layout.buildDirectory.dir("intermediates/javac")) {
            exclude(exclusions)
        },
        fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/debug")) {
            exclude(exclusions)
        }
    ))
    executionData.setFrom(files(
        fileTree(layout.buildDirectory) { include(listOf("**/*.exec", "**/*.ec")) }
    ))
}