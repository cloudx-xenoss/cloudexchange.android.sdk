package io.cloudx.sdk.internal.db.tracking_legacy

import androidx.room.Dao
import androidx.room.Insert

@Dao
internal interface MetricDao {

    @Insert
    suspend fun insert(spend: SpendMetric)
}