## **🚀CloudX Android SDK Installation Guide**

## 📦 CloudX SDK Compatibility

### 🧩 Language Compatibility
- **Kotlin version**: Built with `1.9.22`
    - Host apps should use Kotlin `1.9.0+`
- **Java compatibility**: Compiled to Java 8 bytecode (`jvmTarget = 1.8`)
    - Fully compatible with Java-based host apps (no Kotlin required)

### 📱 Android Compatibility
- **Minimum SDK version**: `21`
- **Compile SDK version**: `35`
    - Host apps must set `minSdkVersion >= 21` to use the SDK.

### 🛠️ Build Compatibility
- **Gradle version**: `8.5`
- **Android Gradle Plugin**: `8.2.2`
    - Host apps using AGP `8.0+` and Gradle `8.0+` are fully supported.

### 🧪 Tested Environment

The SDK has been tested on projects with the following configurations:

| Environment        | Version  |
|--------------------|----------|
| Kotlin             | 1.9.22   |
| Java               | 1.8 & 11 |
| AGP                | 8.2.2    |
| Gradle             | 8.5      |
| Android SDK API    | 21–35    |

---

> 💡 If your app uses older Kotlin, Gradle, or AGP versions and encounters issues, please consider upgrading or contact support for compatibility help.


### 1. Add Maven Central Repository

In your project’s `settings.gradle` or `settings.gradle.kts`  
(You likely already have this by default):

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
```

### 2. Add SDK Dependency
In your app/module `build.gradle` (or `build.gradle.kts`):
```kotlin
dependencies {
    implementation("io.cloudx:sdk:<latest-version>")
}
```

> *Replace* <latest-version> *with the desired version (e.g.* `0.0.1`*).*

### 4. Sync and Build
* Sync your project in Android Studio.
* The SDK will be downloaded automatically from Maven Central.

**That’s it! You’re ready to use the CloudX Android SDK.**

---

### ➡️ Next Steps: Add Adapters

Integrate additional adapters as needed for your ad mediation:

```kotlin
dependencies {
    implementation("io.cloudx:sdk:<latest-version>")

    // Add adapters as needed:
    implementation("io.cloudx:adapter-google:<latest-version>")
    implementation("io.cloudx:adapter-cloudx:<latest-version>")
    implementation("io.cloudx:adapter-mintegral:<latest-version>")
    implementation("io.cloudx:adapter-meta:<latest-version>")
    implementation("io.cloudx:adapter-testbidder:<latest-version>")
}
```

> All adapters are part of this unified repository and published to Maven Central.

## License

This software is licensed under the Elastic License 2.0. See the [LICENSE](./LICENSE) file for details.
