package io.cloudx.sdk.internal

import android.app.Activity
import io.cloudx.sdk.AdViewListener
import io.cloudx.sdk.CloudXAd
import io.cloudx.sdk.Destroyable
import io.cloudx.sdk.internal.adapter.BannerContainer
import io.cloudx.sdk.internal.adapter.BannerFactoryMiscParams
import io.cloudx.sdk.internal.adapter.BidBannerFactory
import io.cloudx.sdk.internal.adapter.BidRequestExtrasProvider
import io.cloudx.sdk.internal.bid.BidApi
import io.cloudx.sdk.internal.bid.BidRequestProvider
import io.cloudx.sdk.internal.cdp.CdpApi
import io.cloudx.sdk.internal.common.BidBackoffMechanism
import io.cloudx.sdk.internal.common.service.ActivityLifecycleService
import io.cloudx.sdk.internal.common.service.AppLifecycleService
import io.cloudx.sdk.internal.config.Config
import io.cloudx.sdk.internal.connectionstatus.ConnectionStatusService
import io.cloudx.sdk.internal.core.ad.source.bid.BidAdSource
import io.cloudx.sdk.internal.core.ad.source.bid.BidAdSourceResponse
import io.cloudx.sdk.internal.core.ad.source.bid.BidBannerSource
import io.cloudx.sdk.internal.core.ad.suspendable.SuspendableBanner
import io.cloudx.sdk.internal.core.ad.suspendable.SuspendableBannerEvent
import io.cloudx.sdk.internal.imp_tracker.ImpressionTracker
import io.cloudx.sdk.internal.tracking.AdEventApi
import io.cloudx.sdk.internal.tracking.MetricsTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

internal interface Banner : Destroyable {

    var listener: AdViewListener?
}

internal fun Banner(
    activity: Activity,
    placementId: String,
    placementName: String,
    bannerContainer: BannerContainer,
    bannerVisibility: StateFlow<Boolean>,
    refreshSeconds: Int,
    adType: AdType,
    preloadTimeMillis: Long,
    bidFactories: Map<AdNetwork, BidBannerFactory>,
    bidRequestExtrasProviders: Map<AdNetwork, BidRequestExtrasProvider>,
    bidMaxBackOffTimeMillis: Long,
    bidAdLoadTimeoutMillis: Long,
    miscParams: BannerFactoryMiscParams,
    bidApi: BidApi,
    cdpApi: CdpApi,
    adEventApi: AdEventApi,
    impressionTracker: ImpressionTracker,
    metricsTracker: MetricsTracker,
    connectionStatusService: ConnectionStatusService,
    activityLifecycleService: ActivityLifecycleService,
    appLifecycleService: AppLifecycleService,
    lineItems: List<Config.LineItem>?,
    accountId: String,
    appKey: String
): Banner {

    val bidRequestProvider = BidRequestProvider(
        activity,
        bidRequestExtrasProviders
    )

    val bidSource =
        BidBannerSource(
            activity,
            bannerContainer,
            refreshSeconds,
            bidFactories,
            placementId,
            placementName,
            adType,
            bidApi,
            cdpApi,
            bidRequestProvider,
            adEventApi,
            impressionTracker,
            metricsTracker,
            miscParams,
            0,
            lineItems,
            accountId,
            appKey
        )

    return BannerImpl(
        activity = activity,
        bidAdSource = bidSource,
        bannerVisibility = bannerVisibility,
        refreshSeconds = refreshSeconds,
        suspendPreloadWhenInvisible = true,
        preloadTimeMillis = preloadTimeMillis,
        bidMaxBackOffTimeMillis = bidMaxBackOffTimeMillis,
        bidAdLoadTimeoutMillis = bidAdLoadTimeoutMillis,
        connectionStatusService = connectionStatusService,
        activityLifecycleService = activityLifecycleService,
        appLifecycleService = appLifecycleService,
    )
}

private class BannerImpl(
    private val activity: Activity,
    private val bidAdSource: BidAdSource<SuspendableBanner>,
    bannerVisibility: StateFlow<Boolean>,
    private val refreshSeconds: Int,
    private val suspendPreloadWhenInvisible: Boolean,
    preloadTimeMillis: Long,
    bidMaxBackOffTimeMillis: Long,
    private val bidAdLoadTimeoutMillis: Long,
    private val connectionStatusService: ConnectionStatusService,
    private val activityLifecycleService: ActivityLifecycleService,
    private val appLifecycleService: AppLifecycleService
) : Banner {

    private val TAG = "BannerImpl"

    private val scope = CoroutineScope(Dispatchers.Main)

    private val bidBackoffMechanism = BidBackoffMechanism()

    override var listener: AdViewListener? = null
        set(listener) {
            field = listener?.decorate()
        }

    init {
        restartBannerRefresh()
    }

    private val bannerRefreshTimer =
        BannerSuspendableTimer(
            activity,
            bannerVisibility,
            activityLifecycleService,
            suspendPreloadWhenInvisible
        )

    private var bannerRefreshJob: Job? = null

    private val refreshDelayMillis = refreshSeconds * 1000L
    private val preloadDelayMillis = (refreshDelayMillis - preloadTimeMillis).coerceAtLeast(0)

    private fun restartBannerRefresh() {
        bannerRefreshJob?.cancel()
        bannerRefreshJob = scope.launch {
            while (true) {
                ensureActive()

                val banner = awaitBackupBanner()

                hideAndDestroyCurrentBanner()
                showNewBanner(banner)

                loadBackupBannerIfAbsent(delayLoadMillis = preloadDelayMillis)

                CloudXLogger.debug(TAG, "Banner refresh scheduled in ${refreshSeconds}s")
                bannerRefreshTimer.awaitTimeout(refreshDelayMillis)
            }
        }
    }

    private val backupBanner = MutableStateFlow<SuspendableBanner?>(null)
    private var backupBannerLoadJob: Job? = null
    private val backupBannerLoadTimer =
        BannerSuspendableTimer(
            activity,
            bannerVisibility,
            activityLifecycleService,
            suspendPreloadWhenInvisible
        )

    private fun loadBackupBannerIfAbsent(delayLoadMillis: Long = 0) {
        if (backupBanner.value != null || backupBannerLoadJob?.isActive == true) {
            return
        }

        backupBannerLoadJob = scope.launch {
            backupBannerLoadTimer.awaitTimeout(delayLoadMillis)

            val banner = loadNewBanner()

            preserveBackupBanner(banner)
        }
    }

    private var backupBannerErrorHandlerJob: Job? = null

    private fun preserveBackupBanner(banner: SuspendableBanner) {
        backupBanner.value = banner

        backupBannerErrorHandlerJob?.cancel()
        backupBannerErrorHandlerJob = scope.launch {
            banner.lastErrorEvent.first { it != null }
            destroyBackupBanner()
            loadBackupBannerIfAbsent()
        }
    }

    private fun destroyBackupBanner() {
        backupBannerErrorHandlerJob?.cancel()

        with(backupBanner) {
            value?.destroy()
            value = null
        }
    }

    private suspend fun awaitBackupBanner(): SuspendableBanner {
        loadBackupBannerIfAbsent()

        val banner = backupBanner.mapNotNull { it }.first()

        backupBannerErrorHandlerJob?.cancel()
        backupBanner.value = null

        return banner
    }

    // Note. Since I'm a douche and don't want to refactor and write a coherent and concise code
    // here's the explanation on what's going on here:
    // Each returned banner from this method should be already attached to the BannerContainer in CloudXAdView.
    // If you look at the implementation of CloudXAdView::createBannerContainer()
    // you'll see that each banner gets inserted to the "background" of the view hence can be treated as invisible/precached.
    // So, first successful non-null tryLoadBanner() call will result in banner displayed on the screen.
    // All the consecutive successful tryLoadBanner() calls will result in banner attached to the background and visibility set to GONE.
    // Once the foreground visible banner is destroyed (banner.destroy())
    // it gets removed from the screen and the next topmost banner gets displayed if available.
    private suspend fun loadNewBanner(): SuspendableBanner = coroutineScope {
        var loadedBanner: SuspendableBanner? = null

        while (loadedBanner == null) {
            ensureActive()

            loadedBanner = bidAdSource.requestBid()?.loadOrDestroyBanner()

            if (loadedBanner == null) {
                bidBackoffMechanism.notifySoftError()

                // Delay after each batch of 3 fails
                CloudXLogger.debug(TAG, "Soft error delay for ${bidBackoffMechanism.getBatchDelay()}ms (batch)")
                delay(bidBackoffMechanism.getBatchDelay())

                if (bidBackoffMechanism.isBatchEnd) {
                    CloudXLogger.debug(TAG, "Batch of 3 soft errors: Delaying for ${bidBackoffMechanism.getBarrierDelay()}ms (barrier pause)")

                    // Additional barrier delay after each batch
                    delay(bidBackoffMechanism.getBarrierDelay())
                }
            } else {
                bidBackoffMechanism.notifySuccess()
            }
        }

        loadedBanner
    }

    /**
     * Trying to load the top rank (1) bid; load the next top one otherwise.
     */
    private suspend fun BidAdSourceResponse<SuspendableBanner>.loadOrDestroyBanner(): SuspendableBanner? =
        coroutineScope {
            for (bidItem in bidItemsByRank) {
                ensureActive()
//                CloudXLogger.debug(
//                    TAG,
//                    "attempting to load ${bidItem.adNetwork} bid of rank: ${bidItem.rank} "
//                )

                val banner = loadOrDestroyBanner(bidAdLoadTimeoutMillis, bidItem.createBidAd)
                if (banner != null) return@coroutineScope banner
            }
            null
        }

    // returns: null - banner wasn't loaded.
    private suspend fun loadOrDestroyBanner(
        loadTimeoutMillis: Long,
        createBanner: suspend () -> SuspendableBanner
    ): SuspendableBanner? {
        // TODO. Replace runCatching with actual check whether ad can be created based on parameters
        //  For instance:
        //  Create AdColony ad when there's no AdColony ad in this module or init parameters are invalid.
        val banner = try {
            createBanner()
        } catch (e: Exception) {
            null
        } ?: return null

        var isBannerLoaded = false

        try {
            // TODO. Await both via flow.combine or alternatives.
            // Waiting for banner's activity to resume/go foreground in order to avoid low banner display rate.
            if (suspendPreloadWhenInvisible) {
                activityLifecycleService.awaitActivityResume(activity)
            } else {
                // Either way don't load banners when the whole app goes background.
                appLifecycleService.awaitAppResume()
            }

            connectionStatusService.awaitConnection()

            isBannerLoaded = withTimeout(loadTimeoutMillis) { banner.load() }

        } catch (e: TimeoutCancellationException) {
            banner.timeout()
        } finally {
            if (!isBannerLoaded) banner.destroy()
        }

        return if (isBannerLoaded) banner else null
    }

    private var currentBanner: SuspendableBanner? = null
    private var currentBannerEventHandlerJob: Job? = null

    private suspend fun showNewBanner(banner: SuspendableBanner) {
        listener?.onAdShowSuccess(CloudXAd(banner.adNetwork))

        currentBanner = banner

        currentBannerEventHandlerJob?.cancel()
        currentBannerEventHandlerJob = scope.launch {
            launch {
                banner.event.filter { it == SuspendableBannerEvent.Click }.collect {
                    listener?.onAdClicked(CloudXAd(banner.adNetwork))
                }
            }
            launch {
                banner.lastErrorEvent.first { it != null }
                hideAndDestroyCurrentBanner()
                restartBannerRefresh()
            }
        }
    }

    private fun hideAndDestroyCurrentBanner() {
        currentBanner?.let { listener?.onAdHidden(CloudXAd(it.adNetwork)) }
        destroyCurrentBanner()
    }

    private fun destroyCurrentBanner() {
        currentBannerEventHandlerJob?.cancel()

        currentBanner?.destroy()
        currentBanner = null
    }

    override fun destroy() {
        scope.cancel()

        destroyCurrentBanner()
        bannerRefreshTimer.destroy()

        destroyBackupBanner()
        backupBannerLoadTimer.destroy()
    }
}

