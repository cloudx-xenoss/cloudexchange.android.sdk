package io.cloudx.sdk.internal.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.cloudx.sdk.internal.ApplicationContext
import io.cloudx.sdk.internal.db.imp_tracking.CachedTrackingEvents
import io.cloudx.sdk.internal.db.imp_tracking.CachedTrackingEventDao
import io.cloudx.sdk.internal.db.metrics.MetricsEvent
import io.cloudx.sdk.internal.db.metrics.MetricsEventDao
import io.cloudx.sdk.internal.db.tracking_legacy.InitOperationStatus
import io.cloudx.sdk.internal.db.tracking_legacy.InitOperationStatusDao
import io.cloudx.sdk.internal.db.tracking_legacy.MetricDao
import io.cloudx.sdk.internal.db.tracking_legacy.Placement
import io.cloudx.sdk.internal.db.tracking_legacy.PlacementDao
import io.cloudx.sdk.internal.db.tracking_legacy.Session
import io.cloudx.sdk.internal.db.tracking_legacy.SessionDao
import io.cloudx.sdk.internal.db.tracking_legacy.SpendMetric

@Database(
    entities = [
        Session::class,
        SpendMetric::class,
        Placement::class,
        InitOperationStatus::class,
        CachedTrackingEvents::class,

        MetricsEvent::class
    ],
    version = 7,
    // Not yet.
    exportSchema = false
)
internal abstract class CloudXDb : RoomDatabase() {

    // region Legacy Metrics
    abstract fun sessionDao(): SessionDao
    abstract fun metricDao(): MetricDao
    abstract fun placementDao(): PlacementDao
    abstract fun initOperationStatusDao(): InitOperationStatusDao
    // endregion

    // region EventTracking
    abstract fun cachedTrackingEventDao(): CachedTrackingEventDao
    // endregion

    // region Metrics
    abstract fun metricsEventDao(): MetricsEventDao
    // endregion
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