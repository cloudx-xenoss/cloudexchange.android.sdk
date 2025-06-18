package io.cloudx.sdk.internal.core.ad.source.bid

import android.app.Activity
import io.cloudx.sdk.Result
import io.cloudx.sdk.internal.AdNetwork
import io.cloudx.sdk.internal.AdType
import io.cloudx.sdk.internal.adapter.BidInterstitialFactory
import io.cloudx.sdk.internal.bid.BidApi
import io.cloudx.sdk.internal.bid.BidRequestProvider
import io.cloudx.sdk.internal.cdp.CdpApi
import io.cloudx.sdk.internal.config.Config
import io.cloudx.sdk.internal.core.ad.source.adapterLoggingDecoration
import io.cloudx.sdk.internal.core.ad.source.baseAdDecoration
import io.cloudx.sdk.internal.core.ad.source.bidAdDecoration
import io.cloudx.sdk.internal.core.ad.source.decorate
import io.cloudx.sdk.internal.core.ad.source.metricsTrackerDecoration
import io.cloudx.sdk.internal.core.ad.suspendable.SuspendableInterstitial
import io.cloudx.sdk.internal.imp_tracker.EventTracker
import io.cloudx.sdk.internal.tracking.AdEventApi
import io.cloudx.sdk.internal.tracking.MetricsTracker

internal fun BidInterstitialSource(
    activity: Activity,
    factories: Map<AdNetwork, BidInterstitialFactory>,
    placementId: String,
    placementName: String,
    requestBid: BidApi,
    cdpApi: CdpApi,
    generateBidRequest: BidRequestProvider,
    adEventApi: AdEventApi,
    eventTracker: EventTracker,
    metricsTracker: MetricsTracker,
    bidRequestTimeoutMillis: Long,
    lineItems: List<Config.LineItem>?,
    accountId: String,
    appKey: String
): BidAdSource<SuspendableInterstitial> {
    val adType = AdType.Interstitial

    return BidAdSource(
        generateBidRequest,
        BidRequestProvider.Params(
            adId = placementId,
            adType = adType,
            placementName,
            lineItems = lineItems,
            accountId = accountId,
            appKey = appKey
        ),
        requestBid,
        cdpApi,
        eventTracker,
        metricsTracker
    ) {

        val price = it.price
        val adNetwork = it.adNetwork
        val adId = it.adId
        val bidId = it.bidId
        val adm = it.adm
        val nurl = it.nurl
        val params = it.params
        val auctionId = it.auctionId

        SuspendableInterstitial(
            price,
            adNetwork,
            adId,
        ) { listener ->
            // TODO. IMPORTANT. Explicit Result cast isn't "cool", even though there's try catch somewhere.
            (factories[adNetwork]?.create(
                activity,
                adId,
                bidId,
                adm,
                params,
                listener
            ) as Result.Success).value
        }.decorate(
            baseAdDecoration() +
                    metricsTrackerDecoration(placementId, price, metricsTracker) +
                    bidAdDecoration(bidId, auctionId, adEventApi, eventTracker) +
                    adapterLoggingDecoration(
                        adUnitId = adId,
                        adNetwork = adNetwork,
                        networkTimeoutMillis = bidRequestTimeoutMillis,
                        type = adType,
                        placementName = placementName,
                        price = price
                    )
        )
    }
}
