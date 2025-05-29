import gradle.kotlin.dsl.accessors._a313ba380b190e27ff4471c793b5aeae.android

plugins {
    `maven-publish`
}

private val releaseVariant = "release"

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
        register<MavenPublication>(releaseVariant) {
            groupId =  libs.mavenGroupId
            // TODO. More robust approach in case project name changes
            artifactId = project.name
            version = libs.sdkVersionName

            afterEvaluate {
                from(components[releaseVariant])
            }
        }
    }
}