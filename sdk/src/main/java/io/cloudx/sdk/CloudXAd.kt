package io.cloudx.sdk

import io.cloudx.sdk.internal.AdNetwork
import io.cloudx.sdk.internal.toPublisherNetworkString

data class CloudXAd(val networkName: String)

// In order to avoid additional changes across the codebase
internal fun CloudXAd(adNetwork: AdNetwork?): CloudXAd =
    CloudXAd(adNetwork.toPublisherNetworkString())