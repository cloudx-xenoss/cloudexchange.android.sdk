package io.cloudx.sdk.internal.imp_tracker

import io.cloudx.sdk.Result
import io.cloudx.sdk.internal.CloudXLogger
import io.cloudx.sdk.internal.Error
import io.cloudx.sdk.internal.Logger
import io.cloudx.sdk.internal.db.CloudXDb
import io.cloudx.sdk.internal.db.imp_tracking.CachedImpression
import io.cloudx.sdk.internal.imp_tracker.model.ImpressionId
import io.cloudx.sdk.internal.imp_tracker.model.encoded
import io.ktor.client.HttpClient
import io.ktor.client.plugins.retry
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import java.util.UUID

internal class ImpressionTrackingApiImpl(
    private val timeoutMillis: Long,
    private val httpClient: HttpClient,
) : ImpressionTrackingApi {

    private val tag = "ImpressionTrackingApi"

    override suspend fun send(
        endpointUrl: String,
        impressionEncoded: String,
        campaignId: String,
        eventValue: Int,
        eventName: String
    ): Result<Unit, Error> {

        Logger.d(tag, buildString {
            appendLine("Sending impression tracking request:")
            appendLine("  Endpoint: $endpointUrl")
            appendLine("  Impression: $impressionEncoded")
            appendLine("  CampaignId: $campaignId")
            appendLine("  EventValue: $eventValue")
            appendLine("  EventName: $eventName")
        })

        return try {
            val response = httpClient.get(endpointUrl) {
                timeout { requestTimeoutMillis = timeoutMillis }
                parameter("impression", impressionEncoded)
                parameter("campaignId", campaignId)
                parameter("eventValue", "1")
                parameter("eventName", eventName)

                retry {
                    retryOnServerErrors(maxRetries = 3)
                    constantDelay(millis = 1000)
                }
            }

            Logger.d(tag, "Request URL: ${response.call.request.url}")

            val responseBody = response.bodyAsText()
            Logger.d(tag, "Tracking response: Status=${response.status}, Body=$responseBody")

            val code = response.status.value
            if (code in 200..299) {
                Result.Success(Unit)
            } else {
                Result.Failure(Error("Bad response status: ${response.status}"))
            }

        } catch (e: Exception) {
            val errStr = "Tracking request failed: ${e.message}"
            Logger.e(tag, errStr)
            Result.Failure(Error(errStr))
        }
    }
}
