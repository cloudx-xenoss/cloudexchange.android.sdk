package io.cloudx.sdk.internal.common.service

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import io.cloudx.sdk.internal.common.CloudXMainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

internal interface AppLifecycleService {

    suspend fun awaitAppResume()
}

internal fun AppLifecycleService(): AppLifecycleService = LazySingleInstance

private val LazySingleInstance by lazy {
    AndroidAppLifecycleService()
}

private class AndroidAppLifecycleService : AppLifecycleService {

    private val isResumed = MutableStateFlow(false)

    private val processLifecycleObserver = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_START -> isResumed.value = true
            Lifecycle.Event.ON_STOP -> isResumed.value = false
            else -> {
                // Nothing to see here.
            }
        }
    }.also {
        CloudXMainScope.launch {
            ProcessLifecycleOwner.get().lifecycle.addObserver(it)
        }
    }

    override suspend fun awaitAppResume() {
        isResumed.first { it }
    }
}