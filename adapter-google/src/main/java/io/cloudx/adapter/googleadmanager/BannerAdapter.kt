package io.cloudx.adapter.googleadmanager

import android.app.Activity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.BaseAdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import io.cloudx.sdk.internal.AdViewSize
import io.cloudx.sdk.internal.adapter.Banner
import io.cloudx.sdk.internal.adapter.BannerContainer
import io.cloudx.sdk.internal.adapter.BannerListener
import io.cloudx.sdk.internal.adapter.CloudXAdError

internal class BannerAdapter(
    private val activity: Activity,
    private val container: BannerContainer,
    private val adUnitId: String,
    private val adViewSize: AdViewSize,
    private val listener: BannerListener
) : Banner {

    private var ad: BaseAdView? = null

    override fun load() {
        val ad = AdManagerAdView(activity)
        this.ad = ad

        with(ad) {
            setAdSizes(
                if (adViewSize == AdViewSize.Standard) AdSize.BANNER else AdSize.MEDIUM_RECTANGLE
            )

            adUnitId = this@BannerAdapter.adUnitId
            adListener = createListener(listener)

            container.onAdd(this)

            loadAd(AdManagerAdRequest.Builder().build())
        }
    }

    private fun createListener(listener: BannerListener?) = object : AdListener() {

        override fun onAdLoaded() {
            super.onAdLoaded()
            listener?.onLoad()
        }

        override fun onAdFailedToLoad(p0: LoadAdError) {
            super.onAdFailedToLoad(p0)
            listener?.onError(CloudXAdError(description = p0.toString()))
        }

        override fun onAdImpression() {
            super.onAdImpression()
            listener?.onShow()
            listener?.onImpression()
        }

        override fun onAdClicked() {
            super.onAdClicked()
            listener?.onClick()
        }
    }

    override fun destroy() {
        ad?.let {
            it.adListener = createListener(null)
            it.destroy()

            container.onRemove(it)
        }

        ad = null
    }
}