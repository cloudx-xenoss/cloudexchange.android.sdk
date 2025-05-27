package io.cloudx.sdk.internal.db.tracking

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

@Entity
internal data class SessionWithMetrics(
    @Embedded
    val session: Session,

    @Relation(
        parentColumn = "id",
        entityColumn = "sessionId"
    )
    val spendMetrics: List<SpendMetric>,

    @Relation(
        parentColumn = "id",
        entityColumn = "sessionId"
    )
    val placements: List<Placement>
)