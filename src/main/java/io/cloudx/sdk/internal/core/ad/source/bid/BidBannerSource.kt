package io.cloudx.sdk.internal.core.ad.source.bid

import android.app.Activity
import io.cloudx.sdk.Result
import io.cloudx.sdk.internal.AdNetwork
import io.cloudx.sdk.internal.AdType
import io.cloudx.sdk.internal.adapter.BannerContainer
import io.cloudx.sdk.internal.adapter.BannerFactoryMiscParams
import io.cloudx.sdk.internal.adapter.BidBannerFactory
import io.cloudx.sdk.internal.bid.BidApi
import io.cloudx.sdk.internal.bid.BidRequestProvider
import io.cloudx.sdk.internal.cdp.CdpApi
import io.cloudx.sdk.internal.config.Config
import io.cloudx.sdk.internal.core.ad.source.adapterLoggingDecoration
import io.cloudx.sdk.internal.core.ad.source.baseAdDecoration
import io.cloudx.sdk.internal.core.ad.source.bidAdDecoration
import io.cloudx.sdk.internal.core.ad.source.decorate
import io.cloudx.sdk.internal.core.ad.source.metricsTrackerDecoration
import io.cloudx.sdk.internal.core.ad.suspendable.SuspendableBanner
import io.cloudx.sdk.internal.imp_tracker.ImpressionTracker
import io.cloudx.sdk.internal.imp_tracker.ImpressionTrackingApi
import io.cloudx.sdk.internal.tracking.AdEventApi
import io.cloudx.sdk.internal.tracking.MetricsTracker

internal fun BidBannerSource(
    activity: Activity,
    bannerContainer: BannerContainer,
    refreshSeconds: Int?,
    factories: Map<AdNetwork, BidBannerFactory>,
    placementId: String,
    placementName: String,
    placementType: AdType,
    requestBid: BidApi,
    cdpApi: CdpApi,
    generateBidRequest: BidRequestProvider,
    adEventApi: AdEventApi,
    impressionTracker: ImpressionTracker,
    metricsTracker: MetricsTracker,
    miscParams: BannerFactoryMiscParams,
    bidRequestTimeoutMillis: Long,
    lineItems: List<Config.LineItem>?,
    accountId: String,
    appKey: String
): BidAdSource<SuspendableBanner> =
    BidAdSource(
        generateBidRequest,
        BidRequestProvider.Params(
            adId = placementId,
            adType = placementType,
            placementName = placementName,
            lineItems = lineItems,
            accountId = accountId,
            appKey = appKey
        ),
        requestBid,
        cdpApi,
        metricsTracker
    ) {

        val price = it.price
        val network = it.adNetwork
        val adId = it.adId
        val bidId = it.bidId
        val adm = it.adm
        val nurl = it.nurl
        val params = it.params
        val auctionId = it.auctionId

        SuspendableBanner(price, network, adId, nurl) { listener ->
            // TODO. Explicit Result cast isn't "cool", even though there's try catch somewhere.
            (factories[network]?.create(
                activity, bannerContainer, refreshSeconds, adId, bidId,
                adm, params, miscParams, listener
            ) as Result.Success).value
        }.decorate(
            baseAdDecoration() +
                    metricsTrackerDecoration(placementId, price, metricsTracker) +
                    bidAdDecoration(bidId, auctionId, adEventApi, impressionTracker) +
                    adapterLoggingDecoration(
                        adUnitId = adId,
                        adNetwork = network,
                        networkTimeoutMillis = bidRequestTimeoutMillis,
                        type = placementType,
                        placementName = placementName,
                        price = price
                    )
        )
    }