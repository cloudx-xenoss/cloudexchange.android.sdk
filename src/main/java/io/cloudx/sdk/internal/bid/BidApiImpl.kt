package io.cloudx.sdk.internal.bid

import io.cloudx.sdk.Result
import io.cloudx.sdk.internal.Error
import io.cloudx.sdk.internal.Logger
import io.cloudx.sdk.internal.imp_tracker.dynamic.TrackingFieldResolver
import io.cloudx.sdk.internal.requestTimeoutMillis
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

internal class BidApiImpl(
    private val endpointUrl: String,
    private val timeoutMillis: Long,
    private val httpClient: HttpClient
) : BidApi {

    private val tag = "BidApiImpl"

    override suspend fun invoke(bidRequest: JSONObject): Result<BidResponse, Error> {

        val requestBody = withContext(Dispatchers.IO) { bidRequest.toString() }
        Logger.d(tag, buildString {
            appendLine("Attempting bid request:")
            appendLine("  Endpoint: $endpointUrl")
            appendLine("  Request Body: $requestBody")
        })

        return try {
            val response = httpClient.post(endpointUrl) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)

                requestTimeoutMillis(timeoutMillis)
            }

            val responseBody = response.bodyAsText()
            Logger.d(tag, buildString {
                appendLine("Received bid response:")
                appendLine("  Status: ${response.status}")
                appendLine("  Body: $responseBody")
            })

            if (response.status == HttpStatusCode.OK) {
                when (val bidResponseResult = jsonToBidResponse(responseBody)) {
                    is Result.Failure -> {
                        val error = bidResponseResult.value
                        Logger.e(tag, "Failed to parse bid response: ${error.description}")
                        Result.Failure(error)
                    }

                    is Result.Success -> {
                        val auctionId = bidResponseResult.value.auctionId
                        TrackingFieldResolver.setResponseData(auctionId, JSONObject(responseBody))
                        bidResponseResult
                    }
                }
            } else {
                val errStr = "Bad response status: ${response.status}"
                Logger.e(tag, errStr)
                Result.Failure(Error(errStr))
            }
        } catch (e: Exception) {
            val errStr = "Request failed: ${e.message}"
            Logger.e(tag, errStr)
            Result.Failure(Error(errStr))
        }
    }
}