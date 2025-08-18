package io.cloudx.adapter.testbidnetwork

import android.app.Activity
import io.cloudx.sdk.internal.AdViewSize
import io.cloudx.sdk.internal.adapter.*
import io.cloudx.sdk.Result

internal object BannerFactory : BidBannerFactory,
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
    ): Result<Banner, String> {
        val banner = StaticBidBanner(
            activity, bannerContainer, adm, listener
        )

        return banner.let { Result.Success(it) }
    }

    override val sizeSupport: List<AdViewSize>
        get() = listOf( AdViewSize.Standard, AdViewSize.MREC)
}