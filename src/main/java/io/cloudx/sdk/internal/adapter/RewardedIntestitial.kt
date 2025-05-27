package io.cloudx.sdk.internal.adapter

import io.cloudx.sdk.Destroyable

interface RewardedInterstitial : AdLoadOperationAvailability, Destroyable {

    fun load()
    fun show()
}

interface RewardedInterstitialListener : AdErrorListener {

    fun onLoad()
    fun onShow()
    fun onImpression()
    fun onEligibleForReward()
    fun onHide()
    fun onClick()
}