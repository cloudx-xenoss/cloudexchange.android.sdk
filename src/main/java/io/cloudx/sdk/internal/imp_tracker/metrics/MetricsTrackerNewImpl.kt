package io.cloudx.sdk.internal.imp_tracker.metrics

import android.util.Log
import com.xor.XorEncryption
import io.cloudx.sdk.Result
import io.cloudx.sdk.internal.Logger
import io.cloudx.sdk.internal.appfgduration.AppForegroundDurationService
import io.cloudx.sdk.internal.config.Config
import io.cloudx.sdk.internal.db.CloudXDb
import io.cloudx.sdk.internal.db.metrics.MetricsEvent
import io.cloudx.sdk.internal.imp_tracker.EventAM
import io.cloudx.sdk.internal.imp_tracker.EventTrackerBulkApi
import io.cloudx.sdk.internal.imp_tracker.EventType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

internal class MetricsTrackerNewImpl(
    private val scope: CoroutineScope,
    private val appForegroundDurationService: AppForegroundDurationService,
    private val eventTrackerBulkApi: EventTrackerBulkApi,
    private val db: CloudXDb
) : MetricsTrackerNew {

    private var endpoint: String? = null
    private var metricsSendJob: Job? = null
    private var sessionId: String = ""
    private var basePayload: String = ""
    private var accountId: String = ""

    override fun setBasicData(sessionId: String, accountId: String, basePayload: String) {
        this.sessionId = sessionId
        this.accountId = accountId
        this.basePayload = basePayload
    }

    override fun start(config: Config) {
        val cycleDurationInSeconds = 20L
        endpoint = "https://tracker-dev.cloudx.io/t/bulk?debug=true"

        Log.d("MetricsTrackerNewImpl", "Starting metrics tracker with cycle duration: $cycleDurationInSeconds seconds")
        metricsSendJob?.cancel()
        metricsSendJob = scope.launch {
            while (true) {
                delay(cycleDurationInSeconds * 1000)
                trySendingPendingMetrics()
            }
        }
    }

    override fun stop() {
        metricsSendJob?.cancel()
        metricsSendJob = null
    }

    override fun trySendingPendingMetrics() {
        Log.d("MetricsTrackerNewImpl", "Attempting to send pending metrics")
        scope.launch {
            val metrics = db.metricsEventDao().getAll()
            Log.d("MetricsTrackerNewImpl", "Found ${metrics.size} pending metrics")
            if (metrics.isEmpty() || endpoint == null) return@launch

            val items = metrics.map { metric -> buildEvent(metric) }

            val result = eventTrackerBulkApi.send(endpoint!!, items)
            if (result is Result.Success) {
                // Delete metrics after successful send
                metrics.forEach { db.metricsEventDao().deleteById(it.id) }
            } else if (result is Result.Failure) {
                Logger.e(
                    "MetricsTrackerNewImpl", "Failed to send metrics: ${result.value.description}"
                )
            }
        }
    }
    override fun trackInitSdkRequest(latency: Long) = trackMetric(MetricsType.SDK_INIT, latency)
    override fun trackGeoRequest(latency: Long) = trackMetric(MetricsType.GEO_API, latency)
    override fun trackBidRequest(latency: Long) = trackMetric(MetricsType.BID_REQUEST, latency)

    // Add more API metric tracking methods as needed using the above pattern

    // Unified metric tracker
    private fun trackMetric(type: MetricsType, latency: Long) {
        Log.d("MetricsTrackerNewImpl", "Tracking metric: ${type.typeCode} with latency: $latency ms")
        scope.launch {
            val metricName = type.typeCode
            val existingMetric = db.metricsEventDao().getAllByMetric(metricName)
            Log.d("MetricsTrackerNewImpl", "Existing metric for $metricName: $existingMetric")
            val updatedMetric = if (existingMetric == null) {
                Log.d("MetricsTrackerNewImpl", "Creating new metric for $metricName")
                MetricsEvent(
                    id = UUID.randomUUID().toString(),
                    metricName = metricName,
                    counter = 1,
                    totalLatency = latency,
                    sessionId = sessionId,
                    auctionId = UUID.randomUUID().toString()
                )
            } else {
                Log.d("MetricsTrackerNewImpl", "Updating existing metric for $metricName")
                existingMetric.apply {
                    counter += 1
                    totalLatency += latency
                }
            }
            db.metricsEventDao().insert(updatedMetric)
        }
    }

    // Build a single EventAM for one metric
    private fun buildEvent(metric: MetricsEvent): EventAM {
        val metricDetail = "${metric.counter}/${metric.totalLatency}"
        val payload = "$basePayload;${metric.metricName};$metricDetail"

        val secret = XorEncryption.generateXorSecret(accountId)
        val campaignId = XorEncryption.generateCampaignIdBase64(accountId)
        val impressionId = XorEncryption.encrypt(payload, secret)

        return EventAM(
            impression = impressionId,
            campaignId = campaignId,
            eventValue = "N/A",
            eventName = EventType.SDK_METRICS.pathSegment,
            type = EventType.SDK_METRICS.pathSegment
        )
    }
}
