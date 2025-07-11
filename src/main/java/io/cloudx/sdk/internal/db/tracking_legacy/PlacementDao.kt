package io.cloudx.sdk.internal.db.tracking_legacy

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
internal interface PlacementDao {

    @Insert
    suspend fun insert(vararg placements: Placement)

    @Query(
        """
            UPDATE placement
            SET
            bidSuccessCount = bidSuccessCount + 1,
            bidSuccessLatencyTotalMillis = bidSuccessLatencyTotalMillis + :latencyMillis   
            WHERE sessionId == :sessionId AND id == :placementId
        """
    )
    suspend fun bidSuccess(sessionId: String, placementId: String, latencyMillis: Long)

    @Query(
        """
            UPDATE placement
            SET
            adLoadSuccessCount = adLoadSuccessCount + 1,
            adLoadSuccessLatencyTotalMillis = adLoadSuccessLatencyTotalMillis + :latencyMillis   
            WHERE sessionId == :sessionId AND id == :placementId
        """
    )
    suspend fun adLoadSuccess(sessionId: String, placementId: String, latencyMillis: Long)

    @Query(
        """
            UPDATE placement
            SET
            adLoadFailedCount = adLoadFailedCount + 1
            WHERE sessionId == :sessionId AND id == :placementId
        """
    )
    suspend fun adLoadFailed(sessionId: String, placementId: String)

    @Query(
        """
            UPDATE placement
            SET
            impressions = impressions + 1
            WHERE sessionId == :sessionId AND id == :placementId
        """
    )
    suspend fun adImpression(sessionId: String, placementId: String)

    @Query(
        """
            UPDATE placement
            SET
            clicks = clicks + 1
            WHERE sessionId == :sessionId AND id == :placementId
        """
    )
    suspend fun adClick(sessionId: String, placementId: String)

    @Query(
        """
            UPDATE placement
            SET
            adTimeToCloseTotalMillis = adTimeToCloseTotalMillis + :timeToCloseMillis
            WHERE sessionId == :sessionId AND id == :placementId
        """
    )
    suspend fun adClose(sessionId: String, placementId: String, timeToCloseMillis: Long)
}