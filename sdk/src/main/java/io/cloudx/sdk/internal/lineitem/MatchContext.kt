package io.cloudx.sdk.internal.lineitem

data class MatchContext(
    val loopIndex: Int,
    val keyValue: Map<String, String>,
    val hashedKeyValues: Map<String, String>,
    val bidderKeyValues: Map<String, Map<String, String>>
)
