package io.cloudx.adapter.testbidnetwork

import android.app.Activity
import io.cloudx.sdk.Result
import io.cloudx.sdk.internal.AdType
import io.cloudx.sdk.internal.AdViewSize
import io.cloudx.sdk.internal.adapter.Banner
import io.cloudx.sdk.internal.adapter.BannerContainer
import io.cloudx.sdk.internal.adapter.BannerFactoryMiscParams
import io.cloudx.sdk.internal.adapter.BannerListener
import io.cloudx.sdk.internal.adapter.BidBannerFactory
import io.cloudx.sdk.internal.adapter.MetaData

internal object NativeAdFactory : BidBannerFactory,
    MetaData by MetaData("test-bid-network-version") {
    // Consider suspend?
    override fun create(
        activity: Activity,
        bannerContainer: BannerContainer,
        refreshSeconds: Int?,
        adId: String,
        bidId: String,
        adm: String,
        params: Map<String, String>?,
        miscParams: BannerFactoryMiscParams,
        listener: BannerListener,
    ): Result<Banner, String> = Result.Success(
        NativeAd(
            activity,
            bannerContainer,
            adm,
            miscParams.adType as AdType.Native,
            listener,
        )
    )

    override val sizeSupport: List<AdViewSize>
        get() = listOf(AdType.Native.Small.size, AdType.Native.Medium.size)
}