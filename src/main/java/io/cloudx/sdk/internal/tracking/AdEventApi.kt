package io.cloudx.sdk.internal.tracking

import io.cloudx.sdk.internal.GlobalScopes
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope

internal interface AdEventApi {

    operator fun invoke(type: EventType, bidId: String)

    enum class EventType {
        Win, Impression
    }
}

internal fun AdEventApi(
    endpointUrl: String,
    timeoutMillis: Long = 5000L,
    retryMax: Int = 3,
    retryDelayMillis: Long = 1000L,
    httpClient: HttpClient = io.cloudx.sdk.internal.httpclient.CloudXHttpClient(),
    scope: CoroutineScope = GlobalScopes.IO,
): AdEventApi =
    AdEventApiImpl(
        endpointUrl,
        timeoutMillis,
        retryMax,
        retryDelayMillis,
        httpClient,
        scope,
    )