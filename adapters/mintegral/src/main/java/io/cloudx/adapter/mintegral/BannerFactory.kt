package io.cloudx.adapter.mintegral

import android.app.Activity
import io.cloudx.sdk.internal.AdViewSize
import io.cloudx.sdk.Result
import io.cloudx.sdk.internal.adapter.Banner
import io.cloudx.sdk.internal.adapter.BannerContainer
import io.cloudx.sdk.internal.adapter.BannerFactoryMiscParams
import io.cloudx.sdk.internal.adapter.BannerListener
import io.cloudx.sdk.internal.adapter.BidBannerFactory
import io.cloudx.sdk.internal.adapter.MetaData

internal object BannerFactory : BidBannerFactory,
    MetaData by MetaData(MintegralVersion) {
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

        BannerAdapter(
            activity,
            bannerContainer,
            placementId = params?.placementId(),
            adUnitId = adm,
            bidId = params?.bidId(),
            miscParams.adViewSize,
            listener,
        )
    )

    override val sizeSupport: List<AdViewSize>
        get() = listOf(AdViewSize.Standard, AdViewSize.MREC)
}