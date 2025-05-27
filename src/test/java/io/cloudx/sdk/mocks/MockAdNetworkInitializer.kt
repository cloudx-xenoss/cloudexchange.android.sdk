package io.cloudx.sdk.mocks

import android.app.Activity
import io.cloudx.sdk.CloudXPrivacy
import io.cloudx.sdk.internal.adapter.AdNetworkInitializer
import io.cloudx.sdk.internal.adapter.InitializationResult
import kotlinx.coroutines.flow.StateFlow

internal class MockAdNetworkInitializer : AdNetworkInitializer {

    override suspend fun initialize(
        activity: Activity,
        config: Map<String, String>,
        privacy: StateFlow<CloudXPrivacy>
    ): InitializationResult {
        return InitializationResult.Success
    }
}
