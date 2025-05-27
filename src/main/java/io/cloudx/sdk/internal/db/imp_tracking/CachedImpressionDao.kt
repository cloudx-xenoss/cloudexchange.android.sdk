package io.cloudx.sdk.internal.db.imp_tracking

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CachedImpressionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(impression: CachedImpression)

    @Query("SELECT * FROM impression_cache_table")
    suspend fun getAll(): List<CachedImpression>

    @Query("DELETE FROM impression_cache_table WHERE id = :id")
    suspend fun delete(id: String)
}
