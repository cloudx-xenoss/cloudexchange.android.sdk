package io.cloudx.sdk.internal.common

private const val SOFT_ERROR_BATCH_SIZE = 3
private const val SOFT_ERROR_BATCH_DELAY_MS = 5_000L
private const val SOFT_ERROR_BARRIER_DELAY_MS = 10_000L

internal class BidBackoffMechanism {
    var softFails: Long = 0
        private set

    fun notifySoftError() {
        softFails++
    }

    fun notifySuccess() {
        softFails = 0
    }

    val isBatchEnd: Boolean
        get() = softFails > 0 && softFails % SOFT_ERROR_BATCH_SIZE == 0L

    fun getBatchDelay(): Long = SOFT_ERROR_BATCH_DELAY_MS
    fun getBarrierDelay(): Long = SOFT_ERROR_BARRIER_DELAY_MS
}
