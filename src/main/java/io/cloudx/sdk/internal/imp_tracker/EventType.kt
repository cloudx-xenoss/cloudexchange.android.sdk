package io.cloudx.sdk.internal.imp_tracker

enum class EventType(val code: String) {
    Impression("imp"),
    Click("click");

    companion object {
        fun from(code: String): EventType? =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) }
    }
}