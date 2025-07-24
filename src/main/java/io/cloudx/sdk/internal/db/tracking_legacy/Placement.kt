package io.cloudx.sdk.internal.db.tracking_legacy

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["id", "sessionId"],
    foreignKeys = [
        ForeignKey(
            entity = Session::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("sessionId")
    ]
)
internal data class Placement(
    val id: String,
    val sessionId: String,
    val bidSuccessCount: Int = 0,
    val bidSuccessLatencyTotalMillis: Long = 0,
    val adLoadSuccessCount: Int = 0,
    val adLoadSuccessLatencyTotalMillis: Long = 0,
    val adLoadFailedCount: Int = 0,
    val impressions: Int = 0,
    val clicks: Int = 0,
    val adTimeToCloseTotalMillis: Long = 0
)