package io.cloudx.demo.demoapp.dynamic

data class AppConfig(
    val appKey: String,
    val layout: Layout,
    val SDKConfiguration: SDKConfiguration
)

data class Layout(
    val screens: Screens
)

data class Screens(
    val banner: BannerScreen? = null,
    val native: NativeScreen? = null,
    val interstitial: DefaultScreen? = null,
    val rewarded: DefaultScreen? = null
)

data class BannerScreen(
    val standard: ArrayList<Placement>? = null,
    val mrec: ArrayList<Placement>? = null
)

data class NativeScreen(
    val small: List<Placement>? = null,
    val medium: List<Placement>? = null
)

data class DefaultScreen(
    val default: List<Placement>? = null
)

data class Placement(
    val placementName: String,
    val adType: String,
    val size: String? = null
)

data class SDKConfiguration(
    val ifa: String? = null,
    val bundle: String? = null,
    val location: Location,
    val userInfo: UserInfo? = null,

    val userKeyValues: Map<String, String>? = null,
    val appKeyValues: Map<String, String>? = null,
)

data class Location(
    val type: String,
    val path: String
)

data class UserInfo(
    val userEmailHashed: String? = null,
    val userEmail: String? = null,
    val userIdRegisteredAtMS: Long? = 0,
    val hashAlgo: String? = null
)
