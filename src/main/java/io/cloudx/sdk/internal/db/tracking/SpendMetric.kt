package io.cloudx.sdk.internal.db.tracking

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
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
internal data class SpendMetric(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val placementId: String,
    val sessionId: String,
    val spend: Double,
    val timestampEpochSeconds: Int
)