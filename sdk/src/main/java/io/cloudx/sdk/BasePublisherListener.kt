package io.cloudx.sdk

interface BasePublisherListener {

    /**
     * Ad was loaded.
     * The [cloudXAd] object, will tell you which network it was.
     */
    fun onAdLoadSuccess(cloudXAd: CloudXAd)

    /**
     * Ad was not loaded. An error happened. You can check details using the [cloudXAdError] object
     */
    fun onAdLoadFailed(cloudXAdError: CloudXAdError)

    /**
     * Ad was shown.
     * The [cloudXAd] object, will tell you which network it was.
     */
    fun onAdShowSuccess(cloudXAd: CloudXAd)

    /**
     * Ad was not shown. An error happened. You can check details using the [cloudXAdError] object
     */
    fun onAdShowFailed(cloudXAdError: CloudXAdError)

    /**
     * Ad was hidden.
     * The [cloudXAd] object, will tell you which network it was.
     */
    fun onAdHidden(cloudXAd: CloudXAd)

    /**
     * Ad was clicked.
     * The [cloudXAd] object, will tell you which network it was.
     */
    fun onAdClicked(cloudXAd: CloudXAd)
}