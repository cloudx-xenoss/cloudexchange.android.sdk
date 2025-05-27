package io.cloudx.sdk.internal.adapter

import android.app.Activity
import io.cloudx.sdk.Result

interface BidInterstitialFactory : MetaData {

    fun create(
        activity: Activity,
        adId: String,
        bidId: String,
        adm: String,
        params: Map<String, String>?,
        listener: InterstitialListener
    ): Result<Interstitial, String>
}

interface BidRewardedInterstitialFactory : MetaData {

    fun create(
        activity: Activity,
        adId: String,
        bidId: String,
        adm: String,
        params: Map<String, String>?,
        listener: RewardedInterstitialListener
    ): Result<RewardedInterstitial, String>
}

interface BidBannerFactory : MetaData, BannerSizeSupport {

    fun create(
        activity: Activity,
        bannerContainer: BannerContainer,
        refreshSeconds: Int?,
        adId: String,
        bidId: String,
        adm: String,
        params: Map<String, String>?,
        miscParams: BannerFactoryMiscParams,
        listener: BannerListener
    ): Result<Banner, String>
}