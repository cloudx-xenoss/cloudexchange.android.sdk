import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration

// DOKKA Documentation generation task

plugins {
    id("org.jetbrains.dokka")
}

// Configure all single-project Dokka tasks at the same time,
// such as dokkaHtml, dokkaJavadoc and dokkaGfm.
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

    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
        // Dokka's stylesheets and assets with conflicting names will be overriden.
        // In this particular case, logo-styles.css will be overriden and ktor-logo.png will
        // be added as an additional image asset
        // customAssets = listOf(file("dokka/logo-icon.svg"))

        footerMessage = "© 2024 CloudX, Inc. All Rights Reserved"
    }
}