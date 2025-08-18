package io.cloudx.adapter.meta

import android.app.Activity
import com.facebook.ads.AudienceNetworkAds
import io.cloudx.sdk.CloudXPrivacy
import io.cloudx.sdk.internal.CloudXLogger
import io.cloudx.sdk.internal.adapter.AdNetworkInitializer
import io.cloudx.sdk.internal.adapter.InitializationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

internal object Initializer : AdNetworkInitializer {
    override suspend fun initialize(
        activity: Activity,
        config: Map<String, String>,
        privacy: StateFlow<CloudXPrivacy>
    ): InitializationResult =
        withContext(Dispatchers.Main) {
            if (isInitialized) {
                CloudXLogger.debug(TAG, "already initialized")
                InitializationResult.Success
            } else {
                privacy.updatePrivacy()

                suspendCancellableCoroutine<InitializationResult> { continuation ->
                    AudienceNetworkAds
                        .buildInitSettings(activity)
                        .withInitListener { initResult ->
                            CloudXLogger.debug(TAG, "initialization status: ${initResult.message}")

                            if (initResult.isSuccess) {
                                isInitialized = true

                                // Sometimes adapters call [Continuation.resume] twice which they shouldn't.
                                // So we have a try catch block around it.
                                try {
                                    continuation.resume(InitializationResult.Success)
                                } catch (e: Exception) {
                                    CloudXLogger.error(TAG, e.toString())
                                }
                            } else {
                                continuation.resume(InitializationResult.Error(initResult.message))
                            }
                        }
                        .initialize()
                }
            }
        }
}

private var isInitialized = false

private const val TAG = "MetaInitializer"

internal const val AudienceNetworkAdsVersion = BuildConfig.AUDIENCE_SDK_VERSION_NAME

private fun StateFlow<CloudXPrivacy>.updatePrivacy() {
    val cloudxPrivacy = value
    // TODO. https://developers.facebook.com/docs/audience-network/optimization/best-practices/coppa
    // AdSettings.setMixedAudience(cloudxPrivacy.isAgeRestrictedUser )

    // TODO. CCPA. https://developers.facebook.com/docs/audience-network/optimization/best-practices/data-processing-options
    // AdSettings.setDataProcessingOptions()
}