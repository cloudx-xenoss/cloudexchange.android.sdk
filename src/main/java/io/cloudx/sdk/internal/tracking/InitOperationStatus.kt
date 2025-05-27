package io.cloudx.sdk.internal.tracking

internal class InitOperationStatus(
    val success: Boolean,
    val startedAtUnixMillis: Long,
    val endedAtUnixMillis: Long,
    val appKey: String,
    /**
     * null - when init operation failed (config not provided); no sessionId to attach to.
     */
    val sessionId: String?
)