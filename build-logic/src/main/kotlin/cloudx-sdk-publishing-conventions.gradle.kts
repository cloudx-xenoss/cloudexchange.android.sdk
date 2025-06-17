import gradle.kotlin.dsl.accessors._a313ba380b190e27ff4471c793b5aeae.android

plugins {
    `maven-publish`
}

private val releaseVariant = "release"

// Read version from command line -PversionName=..., fallback to libs.sdkVersionName
val resolvedVersion = project.findProperty("versionName") as String?
    ?: libs.sdkVersionName

android {
    publishing {
        singleVariant(releaseVariant) {
            withSourcesJar()
            withJavadocJar()
        }
    }
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
