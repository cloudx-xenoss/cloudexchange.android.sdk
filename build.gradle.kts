plugins {
    id("com.android.library")
    kotlin("android")

    `maven-publish`

    id("org.jetbrains.dokka")

    alias(libs.plugins.ksp)
    jacoco
}

private val releaseVariant = "release"
// Read version from command line -PversionName=..., fallback to libs.sdkVersionName
val resolvedVersion = project.findProperty("versionName") as String?
    ?: libs.versions.sdkVersionName

android {
    namespace = libs.versions.sdkPackageName.get()

    compileSdk = 34


    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "SDK_VERSION_NAME", "\"${libs.versions.sdkVersionName.get()}\"")
        buildConfigField("long", "SDK_BUILD_TIMESTAMP", "${System.currentTimeMillis()}")

        val configEndpoint = property("cloudx.endpoint.config")
        buildConfigField("String", "CLOUDX_ENDPOINT_CONFIG", """"$configEndpoint"""")
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
        debug {
            enableAndroidTestCoverage = true
            enableUnitTestCoverage = true
        }
    }
    sourceSets["main"].kotlin {
        srcDir("src/main/samples/kotlin")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    testOptions {
        animationsDisabled = true

        // Tests are run in isolation if this is set but are slower. Comment if you want test to go faster but less precise
        execution = "ANDROIDX_TEST_ORCHESTRATOR"

        unitTests {
            isIncludeAndroidResources = true
        }
    }

    publishing {
        singleVariant(releaseVariant) {
            withSourcesJar()
            withJavadocJar()
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

//    testImplementation(libs.testUnit)
//    androidTestImplementation(libs.testInstrumentation)
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

publishing {
    publications {

        val versionFromTag = System.getenv("GITHUB_REF_NAME") ?: "0.0.1.00"
        version = versionFromTag

        create<MavenPublication>("sdkRelease") {
            groupId = "com.cloudx"
            artifactId = "cloudx-sdk"
            version = versionFromTag

            afterEvaluate {
                from(components["release"])
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/cloudx-xenoss/cloudexchange.android.sdk")
            credentials {
                username = System.getenv("PAT_USERNAME")
                password = System.getenv("PAT_TOKEN")
            }
        }
    }
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    dokkaSourceSets.configureEach {
        moduleName.set("CloudX SDK Android")

        includes.from("dokka/module.md")

        // Do not generate documentation for internal code.
        perPackageOption {
            matchingRegex.set("io.cloudx.sdk.internal.*")
            suppress.set(true)
        }
    }
}

tasks.dokkaHtml {
    outputDirectory.set(buildDir.resolve("dokka/html"))

//    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
//        // Dokka's stylesheets and assets with conflicting names will be overriden.
//        // In this particular case, logo-styles.css will be overriden and ktor-logo.png will
//        // be added as an additional image asset
//        // customAssets = listOf(file("dokka/logo-icon.svg"))
//
//        footerMessage = "© 2024 CloudX, Inc. All Rights Reserved"
//    }
}