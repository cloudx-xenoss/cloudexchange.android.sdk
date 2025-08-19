package io.cloudx.adapter.meta

import android.app.Activity
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.AdListener
import com.facebook.ads.AdSize
import com.facebook.ads.AdView
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
    private var listener: BannerListener?
) : Banner {

    private var ad: AdView? = null

    override fun load() {
        val ad = AdView(
            activity,
            adUnitId,
            if (adViewSize == AdViewSize.Standard) AdSize.BANNER_HEIGHT_50 else AdSize.RECTANGLE_HEIGHT_250
        )
        this.ad = ad

        with(ad) {
            container.onAdd(this)

            loadAd(
                buildLoadAdConfig()
                    .withAdListener(createListener(listener))
                    .build()
            )
        }
    }

    private fun createListener(listener: BannerListener?) = object : AdListener {

        override fun onError(p0: Ad?, p1: AdError?) {
            listener?.onError(CloudXAdError(description = p0.toString()))
        }

        override fun onAdLoaded(p0: Ad?) {
            listener?.onLoad()
        }

        override fun onAdClicked(p0: Ad?) {
            listener?.onClick()
        }

        override fun onLoggingImpression(p0: Ad?) {
            listener?.onShow()
            listener?.onImpression()
        }
    }

    override fun destroy() {
        ad?.let {
            it.destroy()
            container.onRemove(it)
        }
        ad = null

        listener = null
    }
}