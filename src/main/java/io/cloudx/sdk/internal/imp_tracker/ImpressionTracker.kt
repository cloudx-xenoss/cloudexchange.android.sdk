package io.cloudx.sdk.internal.imp_tracker

import io.cloudx.sdk.internal.GlobalScopes
import io.cloudx.sdk.internal.appfgduration.AppForegroundDurationService
import io.cloudx.sdk.internal.db.Database

internal interface ImpressionTracker {

    fun trySendingPendingImpressions()

    fun setEndpoint(endpoint: String?)

    fun send(
        encoded: String,
        campaignId: String,
        eventValue: Int,
        eventName: String
    )
}

internal fun ImpressionTracker(): ImpressionTracker = LazySingleInstance

private val LazySingleInstance by lazy {
    ImpressionTrackerImpl(
        GlobalScopes.IO,
        AppForegroundDurationService(),
        Database()
    )
}