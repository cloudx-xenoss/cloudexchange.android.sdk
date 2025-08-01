package io.cloudx.sdk.mocks

import io.cloudx.sdk.internal.httpclient.UserAgentProvider

internal object MockUserAgentProvider : UserAgentProvider {

    override fun invoke(): String = "Mozilla/5.0 (Linux; Android 13; Pixel 6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Mobile Safari/537.36"
}