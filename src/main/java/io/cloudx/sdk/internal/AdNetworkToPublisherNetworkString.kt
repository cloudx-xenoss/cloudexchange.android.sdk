package io.cloudx.sdk.internal

/**
 * String representation of [AdNetwork] for Public APIs.
 *
 */
internal fun AdNetwork?.toPublisherNetworkString(): String = when (this) {
    AdNetwork.GoogleAdManager -> "GoogleAdManager"
    AdNetwork.TestNetwork -> "TestNetwork"
    AdNetwork.Meta -> "Meta"
    AdNetwork.Mintegral -> "Mintegral"
    AdNetwork.CloudX -> "CloudX"
    AdNetwork.CloudXSecond -> "CloudX2"
    is AdNetwork.Unknown -> name
    null -> ""
}