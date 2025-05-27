package io.cloudx.sdk.internal.bid

import io.cloudx.sdk.Result
import io.cloudx.sdk.internal.Error
import io.cloudx.sdk.internal.httpclient.HttpClient
import org.json.JSONObject

/**
 * Sends ortb compliant Bid request to auction server and gets ortb Bid response (or error) back.
 */
internal interface BidApi {

    // TODO: removed `operator` keyword for better readability. Check consequences!
    suspend fun invoke(bidRequest: JSONObject): Result<BidResponse, Error>
}

internal fun BidApi(endpointUrl: String, timeoutMillis: Long): BidApi = BidApiImpl(
    endpointUrl = endpointUrl,
    timeoutMillis = timeoutMillis,
    httpClient = HttpClient()
)
