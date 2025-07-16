package io.cloudx.sdk.internal

/**
 * [AdNetwork] mapper for network name strings coming from back-end (Config, Bidding APIs etc)
 *
 * @return
 */
internal fun String.toAdNetwork(): AdNetwork = when (this) {
    "testbidder" -> AdNetwork.TestNetwork
    "googleAdManager" -> AdNetwork.GoogleAdManager
    "meta" -> AdNetwork.Meta
    "mintegral" -> AdNetwork.Mintegral
    "cloudx" -> AdNetwork.CloudX
    "cloudxsecond" -> AdNetwork.CloudXSecond
    else -> AdNetwork.Unknown(name = this)
}