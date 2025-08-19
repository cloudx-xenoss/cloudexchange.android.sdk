package io.cloudx.sdk.internal

import io.cloudx.sdk.AdViewListener
import io.cloudx.sdk.CloudXAd
import io.cloudx.sdk.CloudXAdError
import io.cloudx.sdk.InterstitialListener
import io.cloudx.sdk.RewardedInterstitialListener

// TODO. Refactor. Ugly Naming is ugh. Functionality isn't better.

internal fun AdViewListener?.decorate(): AdViewListener =
    this ?: object : AdViewListener {
        override fun onAdLoadSuccess(cloudXAd: CloudXAd) {}

        override fun onAdLoadFailed(cloudXAdError: CloudXAdError) {}

        override fun onAdShowSuccess(cloudXAd: CloudXAd) {}

        override fun onAdShowFailed(cloudXAdError: CloudXAdError) {}

        override fun onAdHidden(cloudXAd: CloudXAd) {}

        override fun onAdClicked(cloudXAd: CloudXAd) {}

        override fun onAdClosedByUser(placementName: String) {}
    }

internal fun InterstitialListener?.decorate(): InterstitialListener =
    this ?: object : InterstitialListener {
        override fun onAdLoadSuccess(cloudXAd: CloudXAd) {}

        override fun onAdLoadFailed(cloudXAdError: CloudXAdError) {}

        override fun onAdShowSuccess(cloudXAd: CloudXAd) {}

        override fun onAdShowFailed(cloudXAdError: CloudXAdError) {}

        override fun onAdHidden(cloudXAd: CloudXAd) {}

        override fun onAdClicked(cloudXAd: CloudXAd) {}
    }

internal fun RewardedInterstitialListener?.decorate(): RewardedInterstitialListener =
    this ?: object : RewardedInterstitialListener {

        override fun onUserRewarded(cloudXAd: CloudXAd) {}

        override fun onAdLoadSuccess(cloudXAd: CloudXAd) {}

        override fun onAdLoadFailed(cloudXAdError: CloudXAdError) {}

        override fun onAdShowSuccess(cloudXAd: CloudXAd) {}

        override fun onAdShowFailed(cloudXAdError: CloudXAdError) {}

        override fun onAdHidden(cloudXAd: CloudXAd) {}

        override fun onAdClicked(cloudXAd: CloudXAd) {}
    }