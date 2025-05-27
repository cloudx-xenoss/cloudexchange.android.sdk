package io.cloudx.sdk.internal.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.cloudx.sdk.internal.ApplicationContext
import io.cloudx.sdk.internal.db.imp_tracking.CachedImpression
import io.cloudx.sdk.internal.db.imp_tracking.CachedImpressionDao
import io.cloudx.sdk.internal.db.tracking.InitOperationStatus
import io.cloudx.sdk.internal.db.tracking.InitOperationStatusDao
import io.cloudx.sdk.internal.db.tracking.MetricDao
import io.cloudx.sdk.internal.db.tracking.Placement
import io.cloudx.sdk.internal.db.tracking.PlacementDao
import io.cloudx.sdk.internal.db.tracking.Session
import io.cloudx.sdk.internal.db.tracking.SessionDao
import io.cloudx.sdk.internal.db.tracking.SpendMetric

@Database(
    entities = [
        Session::class,
        SpendMetric::class,
        Placement::class,
        InitOperationStatus::class,
        CachedImpression::class
    ],
    version = 5,
    // Not yet.
    exportSchema = false
)
internal abstract class CloudXDb : RoomDatabase() {

    abstract fun sessionDao(): SessionDao
    abstract fun metricDao(): MetricDao
    abstract fun placementDao(): PlacementDao

    abstract fun initOperationStatusDao(): InitOperationStatusDao
    abstract fun cachedImpressionDao(): CachedImpressionDao
}

internal fun Database(): CloudXDb = LazySingleInstance

private val LazySingleInstance by lazy {
    Room.databaseBuilder(
        ApplicationContext(),
        CloudXDb::class.java,
        "cloudx"
    )
        .fallbackToDestructiveMigration()
        .build()
}