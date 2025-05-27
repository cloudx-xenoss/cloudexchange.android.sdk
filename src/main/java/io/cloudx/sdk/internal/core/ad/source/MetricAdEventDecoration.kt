package io.cloudx.sdk.internal.core.ad.source

import io.cloudx.sdk.internal.common.utcNowEpochMillis
import io.cloudx.sdk.internal.tracking.MetricsTracker
import kotlin.math.abs

internal fun metricsTrackerDecoration(
    placementId: String,
    price: Double,
    metricsTracker: MetricsTracker
): AdEventDecoration {
    var onStartLoadTime = 0L
    var impressionTime: Long? = null

    return AdEventDecoration(

        onStartLoad = {
            onStartLoadTime = utcNowEpochMillis()
        },

        onLoad = {
            val latencyMillis = abs(utcNowEpochMillis() - onStartLoadTime)
            metricsTracker.adLoadSuccess(placementId, latencyMillis)
        },

        onTimeout = {
            metricsTracker.adLoadFailed(placementId)
        },

        onError = {
            // If error belongs to ad load stage.
            if (impressionTime == null) {
                metricsTracker.adLoadFailed(placementId)
            }
        },

        onImpression = {
            impressionTime = utcNowEpochMillis()
            metricsTracker.spend(placementId, price)
            metricsTracker.adImpression(placementId)
        },

        onClick = {
            metricsTracker.adClick(placementId)
        },

        onHide = {
            val adShowStart = impressionTime ?: return@AdEventDecoration

            val timeToCloseMillis = abs(utcNowEpochMillis() - adShowStart)
            metricsTracker.adClose(placementId, timeToCloseMillis)
        }
    )
}