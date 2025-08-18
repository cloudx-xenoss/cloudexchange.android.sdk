package io.cloudx.demo.demoapp

import io.cloudx.sdk.BasePublisherListener
import io.cloudx.sdk.CloudX
import io.cloudx.sdk.InterstitialListener

class InterstitialFragment : FullPageAdFragment() {

    override fun createAd(listener: BasePublisherListener) = CloudX.createInterstitial(
        requireActivity(),
        placementName,
        object : InterstitialListener, BasePublisherListener by listener {}
    )

    override val adType: String = "Interstitial"
    override val logTag: String = "InterstitialFragment"
}