package io.cloudx.sdk.internal.appfgduration

import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.flow.StateFlow

/**
 * App foreground duration service
 * Tracks how long app was in foreground.
 */
internal interface AppForegroundDurationService {

    val seconds: StateFlow<Long>

    fun start()
}

internal fun AppForegroundDurationService(): AppForegroundDurationService = LazySingleInstance

private val LazySingleInstance by lazy {
    AppForegroundDurationServiceImpl(
        ProcessLifecycleOwner.get().lifecycle
    )
}