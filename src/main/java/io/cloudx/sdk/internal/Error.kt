package io.cloudx.sdk.internal

/**
 * Generic Error class
 */
internal class Error(val description: String, val errorCode: Int = ERROR_CODE_GENERAL)

internal const val ERROR_CODE_GENERAL = -1