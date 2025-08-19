plugins {
    id("com.android.library")
    kotlin("android")
    id("com.vanniktech.maven.publish") version "0.34.0"
}

private val releaseVariant = "release"

android {
    namespace = "io.cloudx.adapter.testbidnetwork"

    compileSdk = 35

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro") // TODO
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

    // Inlined from setupCompileOptions
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    // Inlined from setupKotlinJvmOptions
    kotlinOptions {
        jvmTarget = "1.8"
    }

    // Inlined from setupTestOptions
    testOptions {
        animationsDisabled = true
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

val useRemoteSdk = project.findProperty("useRemoteSdk") == "true"

dependencies {
    if (useRemoteSdk){
        implementation("io.cloudx:sdk:0.0.1.28")
    } else {
        implementation(project(":sdk"))
    }
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.webkit:webkit:1.10.0")
    implementation("androidx.browser:browser:1.7.0")

    // Test dependencies from test-unit
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.10.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.ktor:ktor-client-mock:2.3.7")
    testImplementation("io.mockk:mockk-agent:1.13.8")
    testImplementation("io.mockk:mockk-android:1.13.8")

    // Test dependencies from test-instrumentation
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("io.mockk:mockk-agent:1.13.8")
    androidTestImplementation("io.mockk:mockk-android:1.13.8")
    // Required for ANDROIDX_TEST_ORCHESTRATOR
    androidTestUtil("androidx.test:orchestrator:1.4.2")
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()
    coordinates("io.cloudx", "adapter-testbidder", "0.0.1.27") // Set version from tag if needed

    pom {
        name.set("CloudX Adapter - TestBidder")
        description.set("An Adapter for the CloudX Android SDK: TestBidder Implementation")
        inceptionYear.set("2025")
        url.set("https://github.com/cloudx-xenoss/cloudexchange.android.adapter-testbidder")
        licenses {
            license {
                name.set("Elastic License 2.0")
                url.set("https://www.elastic.co/licensing/elastic-license")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("CloudX")
                name.set("CloudX Team")
                url.set("https://cloudx.io")
            }
        }
        scm {
            url.set("https://github.com/cloudx-xenoss/cloudexchange.android.sdk")
            connection.set("scm:git:git://github.com/cloudx-xenoss/cloudexchange.android.sdk.git")
            developerConnection.set("scm:git:ssh://git@github.com/cloudx-xenoss/cloudexchange.android.sdk.git")
        }
    }
}
