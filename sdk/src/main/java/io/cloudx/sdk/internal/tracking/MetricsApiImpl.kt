package io.cloudx.sdk.internal.tracking

import io.cloudx.sdk.Result
import io.cloudx.sdk.internal.Error
import io.cloudx.sdk.internal.Logger
import io.cloudx.sdk.internal.requestTimeoutMillis
import io.cloudx.sdk.internal.tracking.dtoconverters.toMetricApiJsonString
import io.ktor.client.HttpClient
import io.ktor.client.plugins.retry
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

// TODO. **ApiImpl ctors have lots of common params: timeout, retry etc.
//  Consider extracting them, or provide httpclient with pre-setup builder.
//  Also extract common parts like Bearer Token setup, request builders into a common function/class.
internal class MetricsApiImpl(
    private val timeoutMillis: Long,
    private val retryMax: Int,
    private val retryDelayMillis: Long,
    private val httpClient: HttpClient
) : MetricsApi {

    private val tag = "MetricsApiImpl"

    override suspend fun send(session: Session, params: MetricsApi.Params): Result<Unit, Error> {
        val sessionId = session.id

        val requestBody = session.toMetricApiJsonString()
//        Logger.d(tag, buildString {
//            appendLine("Attempting to send session metric:")
//            appendLine("  Session ID: $sessionId")
//            appendLine("  Endpoint: ${params.endpoint}")
//            appendLine("  AppKey: ${params.appKey}")
//            appendLine("  Request Body: $requestBody")
//        })

        return try {
            val response = httpClient.post(params.endpoint) {
                headers {
                    append("Authorization", "Bearer ${params.appKey}")
                }

                contentType(ContentType.Application.Json)
                setBody(requestBody)

                requestTimeoutMillis(timeoutMillis)

                retry {
                    retryOnServerErrors(maxRetries = retryMax)
                    constantDelay(millis = retryDelayMillis)
                }
            }

//            Logger.d(tag, buildString {
//                appendLine("Received metric response:")
//                appendLine("  Status: ${response.status}")
//                appendLine("  Session ID: $sessionId")
//            })

            if (response.status == HttpStatusCode.OK) {
                Logger.d(tag, "STATUS OK for session: $sessionId")
                Result.Success(Unit)
            } else {
                val errStr = "Bad response status: ${response.status}"
                Logger.e(tag, errStr)
                Result.Failure(Error(errStr))
            }

        } catch (e: Exception) {
            val errStr = "Request failed: ${e.message}"
//            Logger.e(tag, buildString {
//                appendLine("Metric send failed for session:")
//                appendLine("  Session ID: $sessionId")
//                appendLine("  Error: ${e.message}")
//            })
            Result.Failure(Error(errStr))
        }
    }
}