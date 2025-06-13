package io.cloudx.sdk.internal.imp_tracker

import io.cloudx.sdk.Result
import io.cloudx.sdk.internal.Error
import io.ktor.client.HttpClient

/**
 * Impression tracking API to notify backend when a winning ad was rendered.
 */
internal fun interface EventTrackingApi {

    /**
     * Sends a tracking request with encoded impression ID and metadata.
     *
     * @param impressionId - encoded impression data
     * @param campaignId - the ID of the campaign that won
     * @param eventName - name of the event (e.g., "rendered")
     */
    suspend fun send(
        endpointUrl: String,
        encodedData: String,
        campaignId: String,
        eventValue: Int,
        eventName: String
    ): Result<Unit, Error>
}

internal fun TrackingApi(
    timeoutMillis: Long = 10_000,
    httpClient: HttpClient = io.cloudx.sdk.internal.httpclient.CloudXHttpClient(),
): EventTrackingApi = EventTrackingApiImpl(
    timeoutMillis = timeoutMillis,
    httpClient = httpClient
)
