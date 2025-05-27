package io.cloudx.sdk.internal.adapter

import android.view.View
import android.view.ViewGroup
import io.cloudx.sdk.Destroyable
import io.cloudx.sdk.internal.AdType
import io.cloudx.sdk.internal.AdViewSize

interface Banner : Destroyable {

    fun load()
}

interface BannerListener : AdErrorListener {

    fun onLoad()
    fun onShow()
    fun onImpression()
    fun onClick()
}

interface BannerContainer {

    fun onAdd(bannerView: View)
    fun onRemove(bannerView: View)
    fun acquireBannerContainer(): ViewGroup
    fun releaseBannerContainer(bannerContainer: ViewGroup)
}

interface BannerSizeSupport {

    val sizeSupport: List<AdViewSize>
}

class BannerFactoryMiscParams(
    val enforceCloudXImpressionVerification: Boolean,
    val adType: AdType,
    val adViewSize: AdViewSize
)