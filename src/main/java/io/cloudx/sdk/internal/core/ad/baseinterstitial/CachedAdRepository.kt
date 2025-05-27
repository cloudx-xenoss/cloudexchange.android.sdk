package io.cloudx.sdk.internal.core.ad.baseinterstitial

import io.cloudx.sdk.Destroyable
import io.cloudx.sdk.internal.AdType
import io.cloudx.sdk.internal.CloudXLogger
import io.cloudx.sdk.internal.common.BidBackoffAlgorithm
import io.cloudx.sdk.internal.common.service.AppLifecycleService
import io.cloudx.sdk.internal.connectionstatus.ConnectionStatusService
import io.cloudx.sdk.internal.core.ad.AdMetaData
import io.cloudx.sdk.internal.core.ad.source.bid.BidAdSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Updating + caching.
internal class CachedAdRepository<SuspendableAd: Destroyable, C: CacheableAd>(
    private val bidAdSource: BidAdSource<SuspendableAd>,
    cacheSize: Int,
    preCachedAdLifetimeMinutes: Int,
    bidMaxBackOffTimeMillis: Long,
    private val bidLoadTimeoutMillis: Long,
    private val createCacheableAd: suspend (SuspendableAd) -> C,
    connectionStatusService: ConnectionStatusService,
    private val appLifecycleService: AppLifecycleService,
    placementType: AdType,
): Destroyable {

    private val TAG = "Bid$placementType"

    private val scope = CoroutineScope(Dispatchers.Main)
    private val bidBackoffAlgorithm = BidBackoffAlgorithm(bidMaxBackOffTimeMillis)

    private val cachedQueue = CachedAdQueue<C>(
        cacheSize,
        preCachedAdLifetimeMinutes,
        connectionStatusService,
        appLifecycleService,
        placementType
    )

    val hasAds: StateFlow<Boolean> = cachedQueue.hasItems

    val topAdMetaData: AdMetaData? get() = cachedQueue.topAdMeta

    fun popAd(): C? = cachedQueue.popAd()

    init {
        // TODO. Move to CachedQueue?
        scope.launch {
            // Bid ads try to fill the remaining space in the cached queue.
            while (true) {
                ensureActive()

                when (enqueueBid()) {
                    AdLoadOperationStatus.AdLoadSuccess -> {
                        bidBackoffAlgorithm.notifyAdLoadSuccess()
                    }

                    AdLoadOperationStatus.AdLoadOperationUnavailable -> {
                        //TODO this is a workaround. Should be improved.
                        delay(1000)
                    }

                    else -> {
                        bidBackoffAlgorithm.notifyAdLoadFailed()
                        if (bidBackoffAlgorithm.isThreshold) {
                            val delayMillis = bidBackoffAlgorithm.calculateDelayMillis()
                            CloudXLogger.info(
                                TAG,
                                "delaying for: ${delayMillis}ms as ${bidBackoffAlgorithm.bidFails} bid responses have failed to load"
                            )
                            delay(delayMillis)
                        }
                    }
                }
            }
        }
    }

    private suspend fun enqueueBid(): AdLoadOperationStatus = coroutineScope {
        // TODO. Refactor. This whole class and the rest of precaching/displaying stuff..
        //  Move to the BidAdAdSource?
        // Prevent requests for the app in the background.
        appLifecycleService.awaitAppResume()

        // Waiting till cache has available ad slots, then start bid request operation.
        cachedQueue.hasAvailableSlots.first { it }

        // TODO. IMPORTANT.
        //  Rn, We always have bid waiting for cache queue to free up when full.
        bidAdSource.requestBid()?.let {
            // Trying to load the top rank (1) bid; load the next top one otherwise.
            for (bidItem in it.bidItemsByRank) {
                ensureActive()
//                CloudXLogger.debug(
//                    TAG,
//                    "attempting to load ${bidItem.adNetwork} bid of rank: ${bidItem.rank} "
//                )

                val result = cachedQueue.enqueueBidAd(
                    bidItem.price,
                    bidLoadTimeoutMillis,
                    createBidAd = { createCacheableAd(bidItem.createBidAd()) }
                )

                if (result == AdLoadOperationStatus.AdLoadSuccess) {
                    return@coroutineScope result
                }
            }

            AdLoadOperationStatus.AdLoadFailed

        } ?: AdLoadOperationStatus.AdLoadFailed
    }

    override fun destroy() {
        scope.cancel()
        cachedQueue.destroy()
    }
}