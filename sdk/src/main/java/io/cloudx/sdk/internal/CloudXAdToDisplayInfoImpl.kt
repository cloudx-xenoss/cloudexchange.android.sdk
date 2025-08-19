package io.cloudx.sdk.internal

import io.cloudx.sdk.internal.core.ad.baseinterstitial.CachedAdRepository
import io.cloudx.sdk.CloudXAdToDisplayInfoApi

internal val CachedAdRepository<*, *>.adToDisplayInfo: CloudXAdToDisplayInfoApi.Info?
    get() = topAdMetaData?.run {
        CloudXAdToDisplayInfoApi.Info(
            price,
            adNetwork.toPublisherNetworkString()
        )
    }