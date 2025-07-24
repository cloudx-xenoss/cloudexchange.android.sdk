package io.cloudx.sdk.internal.db.tracking_legacy

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
internal data class InitOperationStatus(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val success: Boolean,
    val startedAtUnixMillis: Long,
    val endedAtUnixMillis: Long,
    val appKey: String,
    /**
     * null - when init operation failed (config not provided); no sessionId to attach to.
     */
    val sessionId: String?
)