# Module CloudX SDK Android

CloudX SDK - is a mediation SDK; it allows you to load and display ads from 3rd party bid network adapters.

## Prerequisites

- Android Studio Hedgehog | 2023.1.1 or higher
- minimum supported Android OS level: 21 (Android 5.0)

## CloudX SDK build and distribution

This project uses Maven approach; all adapters and core SDK module already have `maven-plugin` set up
for Maven Local repository distribution.

### Publish to Maven Local

Run Gradle command:

```
./gradlew publishReleasePublicationToMavenLocal
```

For more details on custom Maven repository setup, consider reading [this](https://developer.android.com/build/publish-library/upload-library)

## Add CloudX dependencies to your project

### Maven Local approach

Add respective repositories to your project depending on which CloudX SDK adapters are used.

```
repositories {
   mavenCentral()
   
   // To fetch CloudX SDK dependencies if Maven Local build approach is used. 
   mavenLocal()
   
   // If Mintegral adapter is used, add Mintegral SDK repo:
   maven("https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea")
}
```

In project's `build.gradle.kts` file add all the adapters you want to use:

```
dependencies {
   val version = 0.0.0
   
   // Core SDK dependency.
   implementation("io.cloudx:sdk:$version") 
   
   // List of adapters you need.
   // For example, Mintegral adapter.
   implementation("io.cloudx:adapter-mintegral$version")
}
```

### Gradle Composite build approach

In your project's `settings.gradle.kts` add this line:

```
includeBuild("<path-to-cloudx-project>")
```

Add respective repositories to your project depending on which CloudX SDK adapters are used.

```
repositories {
   mavenCentral()
   
   // If Mintegral adapter is used, add Mintegral SDK repo:
   maven("https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea")
}
```

In your project app module's `build.gradle.kts` add core sdk and adapter dependencies:

```
dependencies {
    // cloudx-sdk is a root project name of CloudX SDK.
    
    // Core SDK dependency.
   implementation("cloudx-sdk:sdk") 
   
   // List of adapters you need.
   // For example, Mintegral adapter.
   implementation("cloudx-sdk:adapter-mintegral")
}
```

For more info, consider reading [this](https://docs.gradle.org/current/userguide/composite_builds.html)

## List of available adapters and supported ad types

|                       |                                          |         |         |              |          |              |               |
|:----------------------|:-----------------------------------------|:-------:|:-------:|:------------:|:--------:|:------------:|:-------------:|
| _Ad network_          | _Gradle dependency_                      | Banner  |  MREC   | Interstitial | Rewarded | Native Small | Native Medium |
| Google Ad Manager     | io.cloudx:adapter-google-admanager:0.0.0 | &check; | &check; |   &check;    | &check;  |   &check;    |    &check;    |
| Meta Audience Network | io.cloudx:adapter-meta:0.0.0             | &check; | &check; |   &check;    | &check;  |      x       |       x       |
| Mintegral             | io.cloudx:adapter-mintegral:0.0.0        | &check; | &check; |   &check;    | &check;  |   &check;    |    &check;    |

## Proguard

All the `consumer-rules.pro` files are already included into the project's modules.
Also, there's `build-logic/common-adapter-consumer-rules.pro` rules applied to each adapter module.

## build-logic module

Build logic contains common gradle rules, plugins, utilities used across the project.

Notable mentions:

- `dokka-conventions.gradle.kts` responsible for [Dokka](https://kotlinlang.org/docs/dokka-get-started.html) KDoc documentation generation
- `cloudx-sdk-publishing-conventions.gradle.kts` sets up Maven plugin for each CloudX SDK module.

## How to use CloudX SDK

Make sure to [initialize](io.cloudx.sdk.CloudX.initialize) SDK first.
To do so, you need to provide:

1. [Activity](android.app.Activity) - Android Context
2. _appKey_ - Identifier of the publisher app registered with Cloudx
3. _initEndpointUrl_ - endpoint to fetch an initial SDK configuration from
4. (optional) initialization [listener](io.cloudx.sdk.CloudXInitializationListener)

### After a successful initialization, now you can create an ad instance of your choice:

- [Banner](io.cloudx.sdk.CloudX.createBanner), [MREC](io.cloudx.sdk.CloudX.createMREC) ads are rectangular ads that occupy a portion of an app's layout. They
  stay on screen while users are interacting with the app, either anchored at the top or bottom of the screen or inline with content as the user scrolls
- Native [Small](io.cloudx.sdk.CloudX.createNativeAdSmall), [Medium](io.cloudx.sdk.CloudX.createNativeAdMedium) ads are ad assets that are presented to users
  through UI components that are native to the platform. They're shown using the same types of views with which you're already building your layouts, and can be
  formatted to match your app's visual design
- [Interstitial](io.cloudx.sdk.CloudX.createInterstitial) ads are full-screen ads that cover the interface of their host app. They're typically displayed at
  natural transition points in the flow of an app, such as between activities or during the pause between levels in a game. When an app shows an interstitial
  ad, the user has the choice to either tap on the ad and continue to its destination or close it and return to the app
- [Rewarded interstitial](io.cloudx.sdk.CloudX.createRewardedInterstitial) ads are fullscreen ads that allow you to reward users with in-app items for
  interacting with video ads, playable ads, and surveys

### Setting the Privacy information in the SDK

1. Call [CloudX.setPrivacy()](io.cloudx.sdk.CloudX.setPrivacy) to set the privacy information whenever it needs to be updated. It is recommended to update
   privacy values before [initialization](io.cloudx.sdk.CloudX.initialize)

```
     CloudX.setPrivacy(
        CloudXPrivacy(
            isUserConsent = true, // user gave consent (GDPR)
            isAgeRestrictedUser = null, // null, flag is not set (COPPA).
            isDoNotSell = true // do not sell my data (CCPA)
        )
    )
```

# Package io.cloudx.sdk

Here reside all the Public APIs of CloudX SDK