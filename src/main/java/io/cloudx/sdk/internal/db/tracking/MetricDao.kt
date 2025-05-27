package io.cloudx.sdk.internal.db.tracking

import androidx.room.Dao
import androidx.room.Insert

@Dao
internal interface MetricDao {

    @Insert
    suspend fun insert(spend: SpendMetric)
}