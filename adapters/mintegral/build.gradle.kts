plugins {
    id("com.android.library")
    kotlin("android")
    id("com.vanniktech.maven.publish") version "0.34.0"
}

private val releaseVariant = "release"


android {
    namespace = "io.cloudx.adapter.mintegral"

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

repositories {
    google()
    mavenCentral()
    mavenLocal()
    maven(url = uri("https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea"))
}

val useRemoteSdk = project.findProperty("useRemoteSdk") == "true"

dependencies {
    if (useRemoteSdk){
        implementation("io.cloudx:sdk:0.0.1.28")
    } else {
        implementation(project(":sdk"))
    }

    // Kotlin coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Mintegral bundle
    implementation("com.mbridge.msdk.oversea:reward:16.7.71")           // mintegral-reward
    implementation("com.mbridge.msdk.oversea:mbbid:16.7.71")            // mintegral-mbbid
    implementation("com.mbridge.msdk.oversea:mbnative:16.7.71")         // mintegral-mbnative
    implementation("com.mbridge.msdk.oversea:newinterstitial:16.7.71")  // mintegral-newinterstitial
    implementation("com.mbridge.msdk.oversea:mbbanner:16.7.71")         // mintegral-mbbanner
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()
    coordinates("io.cloudx", "adapter-mintegral", "0.0.1.00") // Or use version from tag as needed

    pom {
        name.set("CloudX Adapter - Mintegral")
        description.set("An Adapter for the CloudX Android SDK: Mintegral Implementation")
        inceptionYear.set("2025")
        url.set("https://github.com/cloudx-xenoss/cloudexchange.android.sdk")
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
