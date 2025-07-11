package io.cloudx.sdk.internal.db.tracking_legacy

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
internal data class Session(
    @PrimaryKey
    val id: String,
    val durationSeconds: Long,
    // TODO. 99% is the same. Suggest back-end an another approach.
    val trackUrl: String,
    // TODO. 99% is the same. Suggest back-end an another approach.
    val appKey: String
)