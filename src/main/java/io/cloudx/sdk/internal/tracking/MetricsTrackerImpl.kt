package io.cloudx.sdk.internal.tracking

import io.cloudx.sdk.Result
import io.cloudx.sdk.internal.appfgduration.AppForegroundDurationService
import io.cloudx.sdk.internal.common.utcNowEpochMillis
import io.cloudx.sdk.internal.config.Config
import io.cloudx.sdk.internal.db.CloudXDb
import io.cloudx.sdk.internal.db.tracking_legacy.Placement
import io.cloudx.sdk.internal.db.tracking_legacy.Session
import io.cloudx.sdk.internal.db.tracking_legacy.SpendMetric
import io.cloudx.sdk.internal.tracking.dtoconverters.toDB
import io.cloudx.sdk.internal.tracking.dtoconverters.toSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class MetricsTrackerImpl(
    private val scope: CoroutineScope,
    private val appForegroundDurationService: AppForegroundDurationService,
    private val metricsApi: MetricsApi,
    private val db: CloudXDb
) : MetricsTracker {

    private val tag = "MetricsTrackerImpl"

    private var currentSessionId: String? = null

    override suspend fun init(appKey: String, config: Config) {
        if (currentSessionId != null) {
            // Already initialized; session/placement entries created in db.
            return
        }

        val sessionId = config.sessionId

        // Creating new session entry, and respective placement entries participating in this session.
        db.sessionDao().insert(
            Session(
                id = sessionId,
                durationSeconds = 0L,
                trackUrl = config.metricsEndpointUrl,
                appKey = appKey
            )
        )

        config.placements.onEach { (_, placement) ->
            db.placementDao().insert(
                Placement(id = placement.id, sessionId = sessionId)
            )
        }

        currentSessionId = sessionId

        startUpdatingSessionDuration(sessionId)
    }

    private var sessionDurationJob: Job? = null
    private fun startUpdatingSessionDuration(sessionId: String) {
        if (sessionDurationJob?.isActive == true) {
            return
        }
        sessionDurationJob = appForegroundDurationService.seconds.onEach {
            db.sessionDao().updateSessionDuration(sessionId, it)
        }.launchIn(scope)
    }

    override fun spend(placementId: String, price: Double) {
        scope.launch {
            currentSessionId?.let { sessionId ->
                db.metricDao().insert(
                    SpendMetric(
                        placementId = placementId,
                        sessionId = sessionId,
                        spend = price,
                        timestampEpochSeconds = (utcNowEpochMillis() / 1000).toInt()
                    )
                )
            }
        }
    }

    override fun bidSuccess(placementId: String, latencyMillis: Long) {
        scope.launch {
            currentSessionId?.let { sessionId ->
                db.placementDao().bidSuccess(sessionId, placementId, latencyMillis)
            }
        }
    }

    override fun adLoadSuccess(placementId: String, latencyMillis: Long) {
        scope.launch {
            currentSessionId?.let { sessionId ->
                db.placementDao().adLoadSuccess(sessionId, placementId, latencyMillis)
            }
        }
    }

    override fun adLoadFailed(placementId: String) {
        scope.launch {
            currentSessionId?.let { sessionId ->
                db.placementDao().adLoadFailed(sessionId, placementId)
            }
        }
    }

    override fun adImpression(placementId: String) {
        scope.launch {
            currentSessionId?.let { sessionId ->
                db.placementDao().adImpression(sessionId, placementId)
            }
        }
    }

    override fun adClick(placementId: String) {
        scope.launch {
            currentSessionId?.let { sessionId ->
                db.placementDao().adClick(sessionId, placementId)
            }
        }
    }

    override fun adClose(placementId: String, timeToCloseMillis: Long) {
        scope.launch {
            currentSessionId?.let { sessionId ->
                db.placementDao().adClose(sessionId, placementId, timeToCloseMillis)
            }
        }
    }

    override fun initOperationStatus(status: InitOperationStatus) {
        scope.launch {
            db.initOperationStatusDao().insert(status.toDB())
        }
    }

    private var pendingMetricsJob: Job? = null

    override fun trySendPendingMetrics() {
        if (pendingMetricsJob?.isActive == true) {
            return
        }
        pendingMetricsJob = scope.launch {
            val sessionDao = db.sessionDao()

            val currentSessionId = this@MetricsTrackerImpl.currentSessionId

            sessionDao.sessions().filter {
                // Do not send current session metrics,
                // since it's incomplete until process end!
                it.session.id != currentSessionId
            }.onEach {

                val result = metricsApi.send(
                    it.toSession(),
                    MetricsApi.Params(it.session.trackUrl, it.session.appKey)
                )

                if (result is Result.Success) {
                    // Clear db entry since it was sent successfully.
                    sessionDao.delete(it.session)
                }
            }
        }
    }
}