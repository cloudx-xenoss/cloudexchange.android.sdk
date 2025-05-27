package io.cloudx.sdk.internal.adapter

import android.app.Activity
import io.cloudx.sdk.CloudXPrivacy
import kotlinx.coroutines.flow.StateFlow

interface AdNetworkInitializer {

    suspend fun initialize(
        activity: Activity,
        config: Map<String, String>,
        privacy: StateFlow<CloudXPrivacy>
    ): InitializationResult
}

sealed class InitializationResult {
    data object Success : InitializationResult()
    class Error(val error: String = "") : InitializationResult()
}