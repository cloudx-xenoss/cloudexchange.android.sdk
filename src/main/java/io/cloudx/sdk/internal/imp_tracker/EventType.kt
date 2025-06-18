package io.cloudx.sdk.internal.imp_tracker

enum class EventType(
    val pathSegment: String,
    val code: String,
) {
    Click("click", "click"),
    Impression("sdkimp", "imp"),
    BidRequest("bidreq", "bidreq");

    companion object {
        fun from(code: String): EventType? =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) }
    }
}