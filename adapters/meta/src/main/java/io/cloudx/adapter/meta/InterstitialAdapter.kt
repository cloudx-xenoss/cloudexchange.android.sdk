package io.cloudx.adapter.meta

import android.app.Activity
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener
import io.cloudx.sdk.internal.adapter.AdLoadOperationAvailability
import io.cloudx.sdk.internal.adapter.AlwaysReadyToLoadAd
import io.cloudx.sdk.internal.adapter.CloudXAdError
import io.cloudx.sdk.internal.adapter.Interstitial
import io.cloudx.sdk.internal.adapter.InterstitialListener

internal class InterstitialAdapter(
    private val activity: Activity,
    private val adUnitId: String,
    private var listener: InterstitialListener?
) : Interstitial, AdLoadOperationAvailability by AlwaysReadyToLoadAd {

    private var ad: InterstitialAd? = null

    override fun load() {
        val ad = InterstitialAd(activity, adUnitId)
        this.ad = ad

        ad.loadAd(ad.buildLoadAdConfig().withAdListener(object : InterstitialAdListener {
            override fun onError(p0: Ad?, p1: AdError?) {
                listener?.onError(CloudXAdError(description = p1?.errorMessage ?: ""))
            }

            override fun onAdLoaded(p0: Ad?) {
                listener?.onLoad()
            }

            override fun onAdClicked(p0: Ad?) {
                listener?.onClick()
            }

            override fun onLoggingImpression(p0: Ad?) {
                listener?.onImpression()
            }

            override fun onInterstitialDisplayed(p0: Ad?) {
                listener?.onShow()
            }

            override fun onInterstitialDismissed(p0: Ad?) {
                listener?.onHide()
            }
        }).build())
    }

    override fun show() {
        val ad = this.ad
        if (ad == null || !ad.isAdLoaded) {
            listener?.onError(CloudXAdError(description = "can't show: ad is not loaded"))
            return
        }

        // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
        if (ad.isAdInvalidated) {
            listener?.onError(CloudXAdError(description = "can't show: ad is invalidated"))
            return
        }

        // Show the ad
        ad.show()
    }

    override fun destroy() {
        ad?.destroy()
        ad = null
        listener = null
    }
}