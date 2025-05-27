package io.cloudx.sdk.internal.imp_tracker.model

internal data class LocalData(
    val releaseVersion: String,     // ✅ should be SDK version not app version
    val organizationId: String,     // ✅ taken from the SDK config
    val applicationId: String,      // ✅ app package name
    val deviceName: String,         // ✅
    val deviceType: String,         // ✅ "mobile"/"table"/"desktop"
    val osName: String,             // ✅ constant=android
    val osVersion: String,          // ✅ Android OS version
    val sessionId: String,          // ✅ taken from the SDK config
    val testGroupName: String,      // ✅ token from the SDK config (AB dynamic)
    val accountId: String,          // ✅ taken from the SDK config
)

internal data class RequestData(
    val auctionId: String,          // ✅ taken from the BidResponse
    val placementId: String,        // ✅ taken from the SDK config
    val ifa: String,                // ✅
    val loopIndex: Int,             // ✅
)

internal data class BidMetadata(
    val bidId: String,              // ✅ taken from the BidResponse
    val bidder: String,             // ✅ cloudx, cloudxsecond, testbidder
    val priceMicros: String,          // ✅ price
    val responseTimeMillis: Int,    // ✅ bid response time duration
    val width: Int,                 // ✅ ad width and height, if unknown than just placement width and height
    val height: Int,                // ✅ ad width and height, if unknown than just placement width and height
    val dealId: String,             // ✅ taken from the BidResponse or empty
    val creativeId: String          // ✅ taken from the BidResponse or empty. Exactly `creativeId` but not `adm`.
)
