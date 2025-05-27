package io.cloudx.sdk.internal.imp_tracker.model

import android.util.Base64

internal data class ImpressionId(
    val bidder: String = "",
    val width: Int = 0,
    val height: Int = 0,

    val dealId: String = "",
    val creativeId: String = "",
    val cpmMicros: String = "",
    val responseTimeMillis: Int = 0,

    val releaseVersion: String = "",
    val auctionId: String = "",
    val accountId: String = "",
    val organizationId: String = "",
    val applicationId: String = "",
    val placementId: String = "",
    val deviceName: String = "",
    val deviceType: String = "",
    val osName: String = "",
    val osVersion: String = "",
    val sessionId: String = "",
    val ifa: String = "",
    val loopIndex: Int = 0,
    val testGroupName: String = ""
)
internal fun ImpressionId.encoded(): String {

    val values = listOf(
        bidder,
        width,
        height,
        dealId,
        creativeId,
        cpmMicros,
        responseTimeMillis,
        releaseVersion,
        auctionId,
        accountId,
        organizationId,
        applicationId,
        placementId,
        deviceName,
        deviceType,
        osName,
        osVersion,
        sessionId,
        ifa,
        loopIndex,
        testGroupName
    )

    val rawString = values.joinToString(";")
    println("hop: OLD: Raw: $rawString")
    return Base64.encodeToString(rawString.toByteArray(), Base64.NO_WRAP)
}
