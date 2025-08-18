package io.cloudx.adapter.mintegral

import android.app.Activity
import io.cloudx.sdk.internal.adapter.*
import io.cloudx.sdk.Result

internal object RewardedInterstitialFactory :
    BidRewardedInterstitialFactory,
    MetaData by MetaData(MintegralVersion) {

    override fun create(
        activity: Activity,
        adId: String,
        bidId: String,
        adm: String,
        params: Map<String, String>?,
        listener: RewardedInterstitialListener
    ): Result<RewardedInterstitial, String> = Result.Success(
        RewardedInterstitialAdapter(
            activity,
            placementId = params?.placementId(),
            adUnitId = adm,
            bidId = params?.bidId(),
            listener
        )
    )
}