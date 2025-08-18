package io.cloudx.adapter.testbidnetwork

import android.app.Activity
import io.cloudx.sdk.CloudXPrivacy
import io.cloudx.sdk.internal.CloudXLogger
import io.cloudx.sdk.internal.adapter.AdNetworkInitializer
import io.cloudx.sdk.internal.adapter.InitializationResult
import kotlinx.coroutines.flow.StateFlow

object Initializer : AdNetworkInitializer {
    override suspend fun initialize(
        activity: Activity,
        config: Map<String, String>,
        privacy: StateFlow<CloudXPrivacy>
    ): InitializationResult {
        CloudXLogger.info("TestBidNetworkInitializer", "initialized")
        return InitializationResult.Success
    }
}