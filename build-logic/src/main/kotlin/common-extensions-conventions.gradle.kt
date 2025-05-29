import com.android.build.api.dsl.CommonExtension
import gradle.kotlin.dsl.accessors._4a1608c7e4c1251e896c27efcc41b09e.kotlinOptions
import gradle.kotlin.dsl.accessors._a313ba380b190e27ff4471c793b5aeae.kotlinOptions
import org.gradle.api.JavaVersion
import org.gradle.api.artifacts.VersionCatalog

internal fun CommonExtension<*, *, *, *, *>.setupCompileOptions(libs: VersionCatalog) {
    compileOptions {
        val jver = JavaVersion.toVersion(libs.javaVersion)
        sourceCompatibility = jver
        targetCompatibility = jver
    }
}

internal fun com.android.build.gradle.LibraryExtension.setupKotlinJvmOptions(libs: VersionCatalog) {
    kotlinOptions {
        jvmTarget = libs.kotlinJvmTarget
    }
}

internal fun com.android.build.gradle.internal.dsl.BaseAppModuleExtension.setupKotlinJvmOptions(libs: VersionCatalog) {
    kotlinOptions {
        jvmTarget = libs.kotlinJvmTarget
    }
}

internal fun CommonExtension<*, *, *, *, *>.setupTestOptions() {
    testOptions {
        animationsDisabled = true

        // Tests are run in isolation if this is set but are slower. Comment if you want test to go faster but less precise
        execution = "ANDROIDX_TEST_ORCHESTRATOR"

        unitTests {
            isIncludeAndroidResources = true
        }
    }
}