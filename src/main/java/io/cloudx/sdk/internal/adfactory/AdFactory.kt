package io.cloudx.sdk.internal.adfactory

import android.app.Activity
import io.cloudx.sdk.AdViewListener
import io.cloudx.sdk.CloudXAdView
import io.cloudx.sdk.CloudXInterstitialAd
import io.cloudx.sdk.CloudXRewardedAd
import io.cloudx.sdk.InterstitialListener
import io.cloudx.sdk.RewardedInterstitialListener
import io.cloudx.sdk.internal.AdType
import io.cloudx.sdk.internal.common.service.ActivityLifecycleService
import io.cloudx.sdk.internal.common.service.AppLifecycleService
import io.cloudx.sdk.internal.config.Config
import io.cloudx.sdk.internal.connectionstatus.ConnectionStatusService
import io.cloudx.sdk.internal.core.resolver.BidAdNetworkFactories
import io.cloudx.sdk.internal.imp_tracker.EventTracker
import io.cloudx.sdk.internal.tracking.AdEventApi
import io.cloudx.sdk.internal.tracking.MetricsTracker

internal interface AdFactory {
    fun createInterstitial(params: CreateAdParams<InterstitialListener>): CloudXInterstitialAd?
    fun createRewarded(params: CreateAdParams<RewardedInterstitialListener>): CloudXRewardedAd?

    // TODO. Refactor.
    //  For now, to speed things up, I'll use this API to create both Banner and Native Ads.
    fun createBanner(params: CreateBannerParams): CloudXAdView?

    open class CreateAdParams<T>(
        val activity: Activity,
        val placementName: String,
        val listener: T?
    )

    class CreateBannerParams(
        val adType: AdType,
        activity: Activity,
        placementName: String,
        listener: AdViewListener?,
    ) : CreateAdParams<AdViewListener>(
        activity, placementName, listener
    )
}

internal fun AdFactory(
    appKey: String,
    config: Config,
    factories: BidAdNetworkFactories,
    adEventApi: AdEventApi,
    metricsTracker: MetricsTracker,
    eventTracker: EventTracker,
    connectionStatusService: ConnectionStatusService,
    appLifecycleService: AppLifecycleService,
    activityLifecycleService: ActivityLifecycleService
): AdFactory =
    AdFactoryImpl(
        appKey,
        config,
        factories,
        adEventApi,
        metricsTracker,
        eventTracker,
        connectionStatusService,
        appLifecycleService,
        activityLifecycleService
    )