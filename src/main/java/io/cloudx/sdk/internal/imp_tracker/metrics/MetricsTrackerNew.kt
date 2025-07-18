package io.cloudx.sdk.internal.imp_tracker.metrics

import io.cloudx.sdk.internal.GlobalScopes
import io.cloudx.sdk.internal.appfgduration.AppForegroundDurationService
import io.cloudx.sdk.internal.config.Config
import io.cloudx.sdk.internal.db.Database
import io.cloudx.sdk.internal.imp_tracker.EventTrackerBulkApi


internal interface MetricsTrackerNew {

    fun start(config: Config)

    fun setBasicData(sessionId: String, accountId: String, basePayload: String)

    fun trackInitSdkRequest(latency: Long)

    fun trackGeoRequest(latency: Long)

    fun trackBidRequest(latency: Long)

    fun trySendingPendingMetrics()

    fun stop()

}

internal fun MetricsTrackerNew(): MetricsTrackerNew = LazySingleInstance

private val LazySingleInstance by lazy {
    MetricsTrackerNewImpl(
        GlobalScopes.IO,
        AppForegroundDurationService(),
        EventTrackerBulkApi(),
        Database()
    )
}