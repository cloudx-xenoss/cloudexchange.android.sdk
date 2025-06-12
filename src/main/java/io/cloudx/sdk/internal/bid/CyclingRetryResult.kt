package io.cloudx.sdk.internal.bid

import kotlinx.coroutines.delay

sealed class CyclingRetryResult<T> {
    data class Success<T>(val value: T) : CyclingRetryResult<T>()
    data class SoftError<T>(val value: T) : CyclingRetryResult<T>()
    data class HardError<T>(val error: Throwable) : CyclingRetryResult<T>()
}

suspend fun <T> cyclingBarrierRetry(
    maxAttemptsPerGroup: Int = 3,
    delayBetweenAttemptsMs: Long = 10_000,
    delayBetweenGroupsMs: Long = 300_000,
    isHardError: (T) -> Boolean,
    isSoftError: (T) -> Boolean,
    action: suspend (attempt: Int, group: Int) -> T
): CyclingRetryResult<T> {
    var group = 1
    var globalAttempt = 1
    while (true) {
        repeat(maxAttemptsPerGroup) { attemptInGroup ->
            try {
                val result = action(globalAttempt, group)
                when {
                    isHardError(result) -> {
                        if (attemptInGroup < maxAttemptsPerGroup - 1)
                            delay(delayBetweenAttemptsMs)
                    }
                    isSoftError(result) -> {
                        return CyclingRetryResult.SoftError(result)
                    }
                    else -> {
                        return CyclingRetryResult.Success(result)
                    }
                }
            } catch (e: Exception) {
                if (attemptInGroup < maxAttemptsPerGroup - 1)
                    delay(delayBetweenAttemptsMs)
            }
            globalAttempt++
        }
        delay(delayBetweenGroupsMs)
        group++
    }
}
