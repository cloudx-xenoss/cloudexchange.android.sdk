package io.cloudx.sdk.internal

import io.ktor.client.plugins.timeout
import io.ktor.client.request.HttpRequestBuilder

/**
 * Ktor extension for setting timeout for the request
 *
 * @param millis
 */
internal fun HttpRequestBuilder.requestTimeoutMillis(millis: Long) =
    timeout { requestTimeoutMillis = millis }