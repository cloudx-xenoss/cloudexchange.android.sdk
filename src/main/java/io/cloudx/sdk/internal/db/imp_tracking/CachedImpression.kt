package io.cloudx.sdk.internal.db.imp_tracking

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "impression_cache_table")
data class CachedImpression(
    @PrimaryKey val id: String, // e.g. auctionId or UUID
    val encoded: String,
    val campaignId: String,
    val eventName: String,
    val eventValue: Int
)
