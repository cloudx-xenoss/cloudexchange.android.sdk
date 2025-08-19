package io.cloudx.adapter.googleadmanager

import android.app.Activity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import io.cloudx.sdk.internal.AdType
import io.cloudx.sdk.internal.adapter.Banner
import io.cloudx.sdk.internal.adapter.BannerContainer
import io.cloudx.sdk.internal.adapter.BannerListener
import io.cloudx.sdk.internal.adapter.CloudXAdError

internal class NativeAdAdapter(
    private val activity: Activity,
    private val container: BannerContainer,
    private val adUnitId: String,
    private val adType: AdType.Native,
    private val listener: BannerListener
) : Banner {

    private var ad: NativeAd? = null
    private var adView: NativeAdView? = null

    override fun load() {

        val adLoader = AdLoader.Builder(activity, adUnitId)
            .forNativeAd { ad: NativeAd ->
                this@NativeAdAdapter.ad = ad

                val adView = activity.createNativeAdView(adType, ad)
                this@NativeAdAdapter.adView = adView

                container.onAdd(adView)
            }
            .withAdListener(object : AdListener() {

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    listener.onLoad()
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    listener.onError(CloudXAdError(description = p0.toString()))
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    listener.onShow()
                    listener.onImpression()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    listener.onClick()
                }

                override fun onAdSwipeGestureClicked() {
                    super.onAdSwipeGestureClicked()
                    listener.onClick()
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()

        adLoader.loadAd(AdManagerAdRequest.Builder().build())
    }

    override fun destroy() {
        adView?.let {
            container.onRemove(it)
            it.destroy()
        }
        adView = null

        ad?.destroy()
        ad = null

    }
}