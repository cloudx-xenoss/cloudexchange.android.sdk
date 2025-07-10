## **🚀CloudX Android SDK Installation Guide**

### Prerequisite: Get a GitHub Token
* Go to [GitHub PAT Settings](https://github.com/settings/tokens).
* Generate a new token with `read:packages` permission.
* Use your GitHub username and token in the credentials block below.

* The CloudX SDK is built and tested with:
- `minSdkVersion = 21`
- `compileSdkVersion = 35`
- The host app must have a `minSdkVersion >= 21`.
### 1. Add GitHub Maven Repository
In your project’s `settings.gradle` or `settings.gradle.kts`:

```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/cloudx-xenoss/cloudexchange.android.sdk.internal")
            credentials {
                username = "<GITHUB_USERNAME>" 
                password = "<GITHUB_TOKEN>"    
            }
        }
    }
}
```

### 2. Add SDK Dependency
In your app/module `build.gradle` (or `build.gradle.kts`):
```
dependencies {
    implementation("com.cloudx:cloudx-sdk:<latest-version>")
}
```

> *Replace* <latest-version> *with the desired version (e.g.* `v0.0.1.08`*).*

### 4. Sync and Build
* Sync your project in Android Studio.
* The SDK will be downloaded automatically from GitHub Packages.

**That’s it! You’re ready to use the CloudX Android SDK.**

---

### ➡️ Next Steps: Add Adapters

Integrate additional adapters as needed for your ad mediation:

- [Google Adapter](https://github.com/cloudx-xenoss/cloudexchange.android.adapter-google)
- [CloudX Adapter](https://github.com/cloudx-xenoss/cloudexchange.android.adapter-cloudx)
- [Mintegral Adapter](https://github.com/cloudx-xenoss/cloudexchange.android.adapter-mintegral)
- [Meta Adapter](https://github.com/cloudx-xenoss/cloudexchange.android.adapter-meta)

> Visit each link for setup guides and available versions.

## License

This software is licensed under the Elastic License 2.0. See the [LICENSE](./LICENSE) file for details.
