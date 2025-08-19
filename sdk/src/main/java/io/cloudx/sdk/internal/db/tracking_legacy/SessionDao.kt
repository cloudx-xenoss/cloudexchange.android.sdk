package io.cloudx.sdk.internal.db.tracking_legacy

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
internal interface SessionDao {

    @Transaction
    @Query("SELECT * FROM Session")
    suspend fun sessions(): List<SessionWithMetrics>

    @Insert
    suspend fun insert(session: Session)

    @Delete
    suspend fun delete(session: Session)

    @Query(
        """
            UPDATE session
            SET
            durationSeconds = :seconds
            WHERE id == :sessionId
        """
    )
    suspend fun updateSessionDuration(sessionId: String, seconds: Long)
}