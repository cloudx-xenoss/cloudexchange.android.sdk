package io.cloudx.sdk.internal.cdp

import io.cloudx.sdk.Result
import io.cloudx.sdk.internal.Error
import io.cloudx.sdk.internal.httpclient.CloudXHttpClient
import org.json.JSONObject

/**
 * Sends bid payload to CDP Lambda and expects enriched payload back.
 */
internal interface CdpApi {
    suspend fun enrich(original: JSONObject): Result<JSONObject, Error>
}

internal fun CdpApi(endpointUrl: String, timeoutMillis: Long): CdpApi = CdpApiImpl(
    endpointUrl = endpointUrl,
    timeoutMillis = timeoutMillis,
    httpClient = CloudXHttpClient()
)
