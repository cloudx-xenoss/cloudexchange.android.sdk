package io.cloudx.sdk.internal.imp_tracker

enum class EventType(
    val code: String,
    val pathSegment: String,
) {
    Impression("imp", "sdkimp"),
    Click("click", "click");

    companion object {
        fun from(code: String): EventType? =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) }
    }
}