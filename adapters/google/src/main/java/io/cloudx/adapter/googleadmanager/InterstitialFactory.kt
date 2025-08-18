package io.cloudx.adapter.googleadmanager

import android.app.Activity
import io.cloudx.sdk.internal.adapter.*
import io.cloudx.sdk.Result

internal object InterstitialFactory :
    BidInterstitialFactory,
    MetaData by MetaData(AdManagerVersion) {

    override fun create(
        activity: Activity,
        adId: String,
        bidId: String,
        adm: String,
        params: Map<String, String>?,
        listener: InterstitialListener,
    ): Result<Interstitial, String> = Result.Success(
        InterstitialAdapter(
            activity,
            adUnitId = adm,
            listener
        )
    )
}