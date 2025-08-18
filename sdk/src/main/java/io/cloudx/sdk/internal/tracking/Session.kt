package io.cloudx.sdk.internal.tracking

/**
 * Session tied metrics.
 */
internal class Session(
    val id: String,
    val durationSeconds: Long,
    val metrics: List<Metric>
) {

    sealed class Metric {
        class Spend(
            val placementId: String,
            val value: Double,
            val timestampEpochSeconds: Int
        ) : Metric()

        class FillRate(
            val placementIdToValue: Map<String, Double>
        ) : Metric()

        class BidRequestSuccessAverageLatency(
            val placementIdToValue: Map<String, Long>
        ) : Metric()

        class AdLoadSuccessAverageLatency(
            val placementIdToValue: Map<String, Long>
        ) : Metric()

        class AdLoadFailCount(
            val placementIdToValue: Map<String, Int>
        ) : Metric()

        class AdAverageTimeToClose(
            val placementIdToValue: Map<String, Long>
        ) : Metric()

        class ClickThroughRate(
            val placementIdToValue: Map<String, Double>
        ) : Metric()

        class ClickCount(
            val placementIdToValue: Map<String, Int>
        ) : Metric()
    }
}