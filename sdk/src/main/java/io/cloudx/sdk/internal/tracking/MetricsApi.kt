package io.cloudx.sdk.internal.tracking

import io.cloudx.sdk.Result
import io.cloudx.sdk.internal.Error
import io.cloudx.sdk.internal.httpclient.CloudXHttpClient

internal interface MetricsApi {

    suspend fun send(session: Session, params: Params): Result<Unit, Error>

    data class Params(
        val endpoint: String,
        val appKey: String
    )
}

internal fun MetricsApi(): MetricsApi = MetricsApiImpl(
    timeoutMillis = 5000,
    retryMax = 3,
    retryDelayMillis = 1000,
    httpClient = CloudXHttpClient()
)