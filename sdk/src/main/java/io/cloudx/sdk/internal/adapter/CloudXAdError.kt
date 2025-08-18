package io.cloudx.sdk.internal.adapter

interface AdErrorListener {

    fun onError(error: CloudXAdError = CloudXAdError())
}

data class CloudXAdError(val code: AdErrorType = AdErrorType.General, val description: String = "")

enum class AdErrorType {
    Timeout,
    ShowFailed,
    General,
}