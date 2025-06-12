package io.cloudx.sdk.internal.core.ad.source

import io.cloudx.sdk.internal.AdNetwork
import io.cloudx.sdk.internal.AdType
import io.cloudx.sdk.internal.CloudXLogger
import io.cloudx.sdk.internal.GlobalScopes
import io.cloudx.sdk.internal.core.ad.suspendable.SuspendableBanner
import io.cloudx.sdk.internal.core.ad.suspendable.SuspendableInterstitial
import io.cloudx.sdk.internal.core.ad.suspendable.SuspendableRewardedInterstitial
import io.cloudx.sdk.internal.core.ad.suspendable.decorated.DecoratedSuspendableBanner
import io.cloudx.sdk.internal.core.ad.suspendable.decorated.DecoratedSuspendableInterstitial
import io.cloudx.sdk.internal.core.ad.suspendable.decorated.DecoratedSuspendableRewardedInterstitial
import io.cloudx.sdk.internal.imp_tracker.ImpressionTracker
import io.cloudx.sdk.internal.imp_tracker.dynamic.TrackingFieldResolver
import io.cloudx.sdk.internal.tracking.AdEventApi
import kotlinx.coroutines.launch

private typealias Func = (() -> Unit)
private typealias ClickFunc = (() -> Unit)
private typealias ErrorFunc = ((error: io.cloudx.sdk.internal.adapter.CloudXAdError) -> Unit)

class AdEventDecoration(
    val onLoad: Func? = null,
    val onShow: Func? = null,
    val onHide: Func? = null,
    val onImpression: Func? = null,
    val onSkip: Func? = null,
    val onComplete: Func? = null,
    val onReward: Func? = null,
    val onClick: ClickFunc? = null,
    val onError: ErrorFunc? = null,
    val onDestroy: Func? = null,
    // TODO. Oof.
    val onStartLoad: Func? = null,
    val onTimeout: Func? = null,
) {

    operator fun plus(adEventDecoration: AdEventDecoration) = AdEventDecoration(
        {
            onLoad?.invoke()
            adEventDecoration.onLoad?.invoke()
        },
        {
            onShow?.invoke()
            adEventDecoration.onShow?.invoke()
        },
        {
            onHide?.invoke()
            adEventDecoration.onHide?.invoke()
        },
        {
            onImpression?.invoke()
            adEventDecoration.onImpression?.invoke()
        },
        {
            onSkip?.invoke()
            adEventDecoration.onSkip?.invoke()
        },
        {
            onComplete?.invoke()
            adEventDecoration.onComplete?.invoke()
        },
        {
            onReward?.invoke()
            adEventDecoration.onReward?.invoke()
        },
        {
            onClick?.invoke()
            adEventDecoration.onClick?.invoke()
        },
        {
            onError?.invoke(it)
            adEventDecoration.onError?.invoke(it)
        },
        {
            onDestroy?.invoke()
            adEventDecoration.onDestroy?.invoke()
        },
        {
            onStartLoad?.invoke()
            adEventDecoration.onStartLoad?.invoke()
        },
        {
            onTimeout?.invoke()
            adEventDecoration.onTimeout?.invoke()
        },
    )
}

internal fun SuspendableBanner.decorate(adEventDecoration: AdEventDecoration): SuspendableBanner =
    with(adEventDecoration) {
        DecoratedSuspendableBanner(
            onLoad,
            onShow,
            onImpression,
            onClick?.let { { it() } },
            onError,
            onDestroy,
            onStartLoad,
            onTimeout,
            this@decorate
        )
    }

internal fun SuspendableInterstitial.decorate(adEventDecoration: AdEventDecoration): SuspendableInterstitial =
    with(adEventDecoration) {
        DecoratedSuspendableInterstitial(
            onLoad,
            onShow,
            onImpression,
            onSkip,
            onComplete,
            onHide,
            onClick,
            onError,
            onDestroy,
            onStartLoad,
            onTimeout,
            this@decorate,
        )
    }

internal fun SuspendableRewardedInterstitial.decorate(adEventDecoration: AdEventDecoration): SuspendableRewardedInterstitial =
    with(adEventDecoration) {
        DecoratedSuspendableRewardedInterstitial(
            onLoad,
            onShow,
            onImpression,
            onReward,
            onHide,
            onClick,
            onError,
            onDestroy,
            onStartLoad,
            onTimeout,
            this@decorate
        )
    }

fun baseAdDecoration() = AdEventDecoration()

internal fun bidAdDecoration(
    bidId: String,
    auctionId: String,
    adEventApi: AdEventApi,
    impressionTracker: ImpressionTracker,
) = AdEventDecoration(
    onLoad = {
        adEventApi(AdEventApi.EventType.Win, bidId)
    },
    onImpression = {
        adEventApi(AdEventApi.EventType.Impression, bidId)
        val scope = GlobalScopes.IO
        scope.launch {
            TrackingFieldResolver.saveLoadedBid(auctionId, bidId)
            val encodedNewVersion = TrackingFieldResolver.buildEncodedImpressionId(auctionId)

            encodedNewVersion?.let {
                impressionTracker.send(it, "c1", 1, "imp")
            }
        }
    }
)

internal fun adapterLoggingDecoration(
    adUnitId: String,
    adNetwork: AdNetwork,
    networkTimeoutMillis: Long,
    type: AdType,
    placementName: String,
    price: Double,
): AdEventDecoration {
    val tag = "${adNetwork}${type}Adapter"

    return AdEventDecoration(
        onTimeout = {
            CloudXLogger.debug(
                tag,
                "LOAD TIMEOUT placement: $placementName, id: $adUnitId, price: $price"
            )
        },
        onLoad = {
            CloudXLogger.debug(
                tag,
                "LOAD SUCCESS placement: $placementName, id: $adUnitId, price: $price"
            )
        },
        onError = {
            CloudXLogger.error(
                tag,
                "ERROR placement: $placementName, id: $adUnitId, price: $price, error: ${it.description}"
            )
        },
        onImpression = {
            CloudXLogger.debug(
                tag,
                "IMPRESSION placement: $placementName, id: $adUnitId, price: $price"
            )
        },
    )
}