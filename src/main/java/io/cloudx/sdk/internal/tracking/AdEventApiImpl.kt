package io.cloudx.sdk.internal.tracking

import android.net.Uri
import io.cloudx.sdk.internal.Logger
import io.cloudx.sdk.internal.requestTimeoutMillis
import io.ktor.client.HttpClient
import io.ktor.client.plugins.retry
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class AdEventApiImpl(
    private val endpointUrl: String,
    private val timeoutMillis: Long,
    private val retryMax: Int,
    private val retryDelayMillis: Long,
    private val httpClient: HttpClient,
    private val scope: CoroutineScope,
) : AdEventApi {

    private val TAG = "AdEventApiImpl"

    override fun invoke(type: AdEventApi.EventType, bidId: String) {
        scope.launch(Dispatchers.IO) {
            val builtUrl = try {
                Uri.parse(endpointUrl)
                    .buildUpon()
                    .appendQueryParameter("t", type.stringify())
                    .appendQueryParameter("b", bidId)
                    .build().toString()
            } catch (e: Exception) {
                Logger.e(TAG, "Malformed URL for event type: $type, bidId: $bidId, error: $e")
                return@launch
            }

            Logger.d(TAG, buildString {
                appendLine("Attempting ad event request:")
                appendLine("  Event Type: ${type.stringify()}")
                appendLine("  Bid ID: $bidId")
                appendLine("  URL: $builtUrl")
            })

            try {
                val response = httpClient.get(builtUrl) {
                    requestTimeoutMillis(timeoutMillis)

                    retry {
                        retryOnServerErrors(maxRetries = retryMax)
                        constantDelay(millis = retryDelayMillis)
                    }
                }

                Logger.d(TAG, buildString {
                    appendLine("Received ad event response:")
                    appendLine("  Status: ${response.status}")
                    appendLine("  URL: $builtUrl")
                })
            } catch (e: Exception) {
                Logger.e(TAG, buildString {
                    appendLine("HTTP request failed for event:")
                    appendLine("  Event Type: ${type.stringify()}")
                    appendLine("  Bid ID: $bidId")
                    appendLine("  Error: ${e.message}")
                })
            }
        }
    }
}

private fun AdEventApi.EventType.stringify() = when (this) {
    AdEventApi.EventType.Win -> "win"
    AdEventApi.EventType.Impression -> "imp"
}