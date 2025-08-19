package io.cloudx.sdk.internal.db.tracking_legacy

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert

@Dao
internal interface InitOperationStatusDao {

    @Insert
    suspend fun insert(initOperationStatus: InitOperationStatus)

    @Delete
    suspend fun delete(initOperationStatus: InitOperationStatus)
}