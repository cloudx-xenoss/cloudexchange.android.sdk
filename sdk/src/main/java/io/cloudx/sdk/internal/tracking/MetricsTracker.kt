package io.cloudx.sdk.internal.tracking

import io.cloudx.sdk.internal.GlobalScopes
import io.cloudx.sdk.internal.appfgduration.AppForegroundDurationService
import io.cloudx.sdk.internal.config.Config
import io.cloudx.sdk.internal.db.Database

/**
 * Main hub for collecting, tracking and reporting detailed ad/user performance metrics.
 *
 * @constructor Create empty Metrics tracker
 */
internal interface MetricsTracker {

    fun initOperationStatus(status: InitOperationStatus)

    /**
     * Start tracking new app session.
     *
     * __Call this function first__ before calling anything else (_Doesn't apply to [trySendPendingMetrics], [initOperationStatus]_)
     * in order not to miss some metrics/events.
     *
     * @param appKey - app key, CloudX was initialized with.
     * @param config - SDK init config, contains session id data, metric endpoint url etc.
     */
    suspend fun init(appKey: String, config: Config)

    /**
     * Track ad spend.
     *
     * @param placementId - [Config.Placement.id] this impression event is linked to.
     * @param price - impression price
     */
    fun spend(placementId: String, price: Double)

    /**
     * @param latencyMillis time it took to get a successful bid response.
     */
    fun bidSuccess(placementId: String, latencyMillis: Long)

    /**
     * @param latencyMillis time it took to load ad successfully.
     */
    fun adLoadSuccess(placementId: String, latencyMillis: Long)

    fun adLoadFailed(placementId: String)

    /**
     * Track impression.
     *
     * @param placementId - [Config.Placement.id] this impression event is linked to.
     */
    fun adImpression(placementId: String)

    fun adClick(placementId: String)

    /**
     * @param timeToCloseMillis time it took to close the ad; counting starts from ad display.
     */
    fun adClose(placementId: String, timeToCloseMillis: Long)

    /**
     * Some metrics might be stored locally; awaiting for decision from the caller.
     * This functions sends accumulated/stored pending metrics if available, right away;
     * clears them from the storage upon success.
     */
    fun trySendPendingMetrics()
}

internal fun MetricsTracker(): MetricsTracker = LazySingleInstance

private val LazySingleInstance by lazy {
    MetricsTrackerImpl(
        GlobalScopes.IO,
        AppForegroundDurationService(),
        MetricsApi(),
        Database()
    )
}