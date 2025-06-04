package io.cloudx.sdk.internal.geo

import io.cloudx.sdk.Result
import io.cloudx.sdk.internal.Error
import io.cloudx.sdk.internal.Logger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.retry
import io.ktor.client.plugins.timeout
import io.ktor.client.request.head
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

internal class GeoApiImpl(
    private val timeoutMillis: Long,
    private val retryMax: Int,
    private val httpClient: HttpClient
) : GeoApi {

    private val tag = "GeoApiImpl"

    override suspend fun fetchGeoHeaders(endpointUrl: String): Result<Map<String, String>, Error> {
        Logger.d(tag, "Fetching geo headers from $endpointUrl")

        return try {
            val response: HttpResponse = httpClient.head(endpointUrl) {
                timeout { requestTimeoutMillis = timeoutMillis }
                retry {
                    maxRetries = retryMax
                    exponentialDelay()
                    retryOnException(retryOnTimeout = true)
                    retryIf { _, httpResponse ->
                        httpResponse.status.value in 500..599 ||
                                httpResponse.status == HttpStatusCode.RequestTimeout ||
                                httpResponse.status == HttpStatusCode.NotFound
                    }
                }
            }

            // Return ALL response headers as a map
            val headersMap = response.headers.entries()
                .associate { (key, values) -> key to values.joinToString(",") }

            Logger.d(tag, "Fetched headers: $headersMap")

            if (response.status == HttpStatusCode.OK && headersMap.isNotEmpty()) {
                Result.Success(headersMap)
            } else {
                Result.Failure(Error("No headers found in response"))
            }
        } catch (e: Exception) {
            Logger.e(tag, "Geo fetch failed: ${e.message}")
            Result.Failure(Error("Geo fetch failed: ${e.message}"))
        }
    }
}
