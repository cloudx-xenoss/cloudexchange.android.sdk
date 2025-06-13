package io.cloudx.sdk.internal.imp_tracker

import io.cloudx.sdk.Result
import io.cloudx.sdk.internal.CloudXLogger
import io.cloudx.sdk.internal.Error
import io.cloudx.sdk.internal.Logger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.retry
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText

internal class EventTrackingApiImpl(
    private val timeoutMillis: Long,
    private val httpClient: HttpClient,
) : EventTrackingApi {

    private val tag = "EventTrackingApi"

    override suspend fun send(
        endpointUrl: String,
        encodedData: String,
        campaignId: String,
        eventValue: Int,
        eventName: String
    ): Result<Unit, Error> {

        Logger.d(tag, buildString {
            appendLine("Sending event tracking  request:")
            appendLine("  Endpoint: $endpointUrl")
            appendLine("  Impression: $encodedData")
            appendLine("  CampaignId: $campaignId")
            appendLine("  EventValue: $eventValue")
            appendLine("  EventName: $eventName")
        })

        CloudXLogger.info("MainActivity", "Tracking: Sent $eventName event")

        return try {
            val response = httpClient.get(endpointUrl) {
                timeout { requestTimeoutMillis = timeoutMillis }
                parameter("impression", encodedData)
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
