package io.cloudx.sdk.internal.common

private const val DELAY_MILLIS: Long = 1000
private const val MAX_BID_FAILS = 10

/**
 * Algorithm to backoff retrying ad.load() when there have been a lot of adLoadFailed events.
 */
internal class BidBackoffAlgorithm(private val bidMaxBackOffTimeMillis: Long) {

    var bidFails: Long = 0
        private set

    /**
     * Currently set recommended delay for bid ads
     */
    var currentDelayMillis: Long = 0
        private set

    /**
     * Checks if there has been more than [MAX_BID_FAILS] of adFailed events.
     * Those events are gathered by calling [notifyAdLoadSuccess] and [notifyAdLoadFailed] respectively.
     */
    val isThreshold: Boolean
        get() = bidFails > 0 && bidFails % MAX_BID_FAILS == 0L

    fun notifyAdLoadSuccess() {
        currentDelayMillis = 0
        bidFails = 0
    }

    fun notifyAdLoadFailed() {
        bidFails++
    }

    /**
     * Calculates and returns the recommended delay millis for bids.
     * Should be called only if [isThreshold] returns true.
     *
     * @return [currentDelayMillis]
     */
    fun calculateDelayMillis(): Long {
        currentDelayMillis += DELAY_MILLIS

        //  we only  allow a maximum of delay
        if (currentDelayMillis > bidMaxBackOffTimeMillis) {
            currentDelayMillis = bidMaxBackOffTimeMillis
        }

        return currentDelayMillis
    }
}