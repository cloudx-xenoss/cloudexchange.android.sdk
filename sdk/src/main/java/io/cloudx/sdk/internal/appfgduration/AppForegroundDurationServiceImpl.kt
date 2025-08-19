package io.cloudx.sdk.internal.appfgduration

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.time.DurationUnit
import kotlin.time.toDuration

internal class AppForegroundDurationServiceImpl(
    private val appLifecycle: Lifecycle
) : AppForegroundDurationService {

    override val seconds = MutableStateFlow(0L)

    private var job: Job? = null

    override fun start() {
        job?.cancel()
        job = appLifecycle.coroutineScope.launch {
            appLifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    val secondsPassed = 5
                    delay(secondsPassed.toDuration(DurationUnit.SECONDS))

                    seconds.value += secondsPassed
                }
            }
        }
    }
}