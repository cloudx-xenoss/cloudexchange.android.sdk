package io.cloudx.sdk.internal.tracking.dtoconverters

import io.cloudx.sdk.internal.db.tracking_legacy.SessionWithMetrics
import io.cloudx.sdk.internal.tracking.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

internal suspend fun SessionWithMetrics.toSession(): Session = withContext(Dispatchers.IO) {

    val fillRate = Session.Metric.FillRate(
        placements.map { p ->
            val bids = p.bidSuccessCount
            p.id to if (bids == 0) 0.0 else p.impressions / bids.toDouble() * 100
        }.associate {
            it.first to it.second
        }
    )

    val bidRequestSuccessAverageLatency = Session.Metric.BidRequestSuccessAverageLatency(
        placements.map { p ->
            val bids = p.bidSuccessCount
            p.id to if (bids == 0) 0 else p.bidSuccessLatencyTotalMillis / bids
        }.associate {
            it.first to it.second
        }
    )

    val adLoadSuccessAverageLatency = Session.Metric.AdLoadSuccessAverageLatency(
        placements.map { p ->
            val adLoads = p.adLoadSuccessCount
            p.id to if (adLoads == 0) 0 else p.adLoadSuccessLatencyTotalMillis / adLoads
        }.associate {
            it.first to it.second
        }
    )

    val adLoadFailCount = Session.Metric.AdLoadFailCount(
        placements.map { p ->
            p.id to p.adLoadFailedCount
        }.associate {
            it.first to it.second
        }
    )

    val adAverageTimeToClose = Session.Metric.AdAverageTimeToClose(
        placements.map { p ->
            val imps = p.impressions
            p.id to if (imps == 0) 0 else p.adTimeToCloseTotalMillis / imps
        }.associate {
            it.first to it.second
        }
    )

    val clickThroughRate = Session.Metric.ClickThroughRate(
        placements.map { p ->
            val imps = p.impressions
            p.id to if (imps == 0) 0.0 else p.clicks / imps.toDouble() * 100
        }.associate {
            it.first to it.second
        }
    )

    val clickCount = Session.Metric.ClickCount(
        placements.map { p ->
            p.id to p.clicks
        }.associate {
            it.first to it.second
        }
    )

    Session(
        session.id,
        session.durationSeconds,
        metrics = spendMetrics.map {
            Session.Metric.Spend(
                it.placementId,
                it.spend,
                it.timestampEpochSeconds
            )
        } + listOf(
            fillRate,
            bidRequestSuccessAverageLatency,
            adLoadSuccessAverageLatency,
            adLoadFailCount,
            adAverageTimeToClose,
            clickThroughRate,
            clickCount
        )
    )
}

internal suspend fun Session.toMetricApiJsonString(): String = with(Dispatchers.IO) {

    val session = this@toMetricApiJsonString

    JSONObject().apply {
        put("session", JSONObject().apply {
            put("ID", session.id)
            put("duration", session.durationSeconds)

            put("metrics", JSONArray().apply {
                session.metrics.onEach { metric ->
                    put(JSONObject().apply {
                        when (metric) {
                            is Session.Metric.Spend -> {
                                put("type", "spend")
                                put("placementID", metric.placementId)
                                put("value", metric.value)
                                put("timestamp", metric.timestampEpochSeconds)
                            }

                            is Session.Metric.FillRate -> putMetric("fill_rate", metric.placementIdToValue)
                            is Session.Metric.BidRequestSuccessAverageLatency -> putMetric("bid_request_success_avg_latency", metric.placementIdToValue)
                            is Session.Metric.AdLoadSuccessAverageLatency -> putMetric("ad_load_success_avg_latency", metric.placementIdToValue)
                            is Session.Metric.AdLoadFailCount -> putMetric("ad_load_fail_count", metric.placementIdToValue)
                            is Session.Metric.AdAverageTimeToClose -> putMetric("ad_avg_time_to_close", metric.placementIdToValue)
                            is Session.Metric.ClickThroughRate -> putMetric("ctr", metric.placementIdToValue)
                            is Session.Metric.ClickCount -> putMetric("click_count", metric.placementIdToValue)
                        }
                    })
                }
            })
        })
    }.toString()
}

private fun JSONObject.putMetric(metricName: String, placementIdToValue: Map<String, Any>) {
    put("type", metricName)
    put("by_placement_id", JSONObject().apply {
        placementIdToValue.onEach {
            put(it.key, it.value)
        }
    })
}