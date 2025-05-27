package io.cloudx.sdk.internal.imp_tracker

import io.cloudx.sdk.internal.Logger
import io.cloudx.sdk.internal.appfgduration.AppForegroundDurationService
import io.cloudx.sdk.internal.db.CloudXDb
import io.cloudx.sdk.internal.db.imp_tracking.CachedImpression
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.UUID

internal class ImpressionTrackerImpl(
    private val scope: CoroutineScope,
    private val appForegroundDurationService: AppForegroundDurationService,
    private val db: CloudXDb
) : ImpressionTracker {

    private val tag = "ImpressionTrackerImpl"
    private var endpoint: String? = null

    private val trackingApi = ImpressionTrackingApi()

    override fun send(
        encoded: String,
        campaignId: String,
        eventValue: Int,
        eventName: String
    ) {
        scope.launch {
            endpoint?.let {
                val result = trackingApi.send(it, encoded, campaignId, eventValue, eventName)

                if (result is io.cloudx.sdk.Result.Success) {
                    Logger.d(tag, "Impression sent successfully.")
                } else {
                    Logger.e(tag, "Impression failed to send. Caching for retry later.")
                    saveToDb(encoded, campaignId, eventValue, eventName)
                }
            } ?: run {
                saveToDb(encoded, campaignId, eventValue, eventName)
            }
        }
    }

    override fun trySendingPendingImpressions() {
        scope.launch {
            val cached = db.cachedImpressionDao().getAll()

            if (cached.isEmpty()) {
                Logger.d(tag, "No pending impressions to send")
                return@launch
            }

            Logger.d(tag, "Found ${cached.size} pending impressions to retry")

            endpoint?.let {
                for (entry in cached) {
                    val result = trackingApi.send(
                        it,
                        entry.encoded,
                        entry.campaignId,
                        entry.eventValue,
                        entry.eventName
                    )

                    if (result is io.cloudx.sdk.Result.Success) {
                        Logger.d(tag, "Successfully resent cached impression: ${entry.id}")
                        db.cachedImpressionDao().delete(entry.id)
                    } else {
                        Logger.e(
                            tag,
                            "Retry failed for cached impression: ${entry.id}, will keep for later"
                        )
                    }
                }
            }
        }
    }

    override fun setEndpoint(endpoint: String?) {
        this.endpoint = endpoint
    }

    private suspend fun saveToDb(
        encoded: String,
        campaignId: String,
        eventValue: Int,
        eventName: String
    ) {
        db.cachedImpressionDao().insert(
            CachedImpression(
                id = UUID.randomUUID().toString(),
                encoded = encoded,
                campaignId = campaignId,
                eventValue = eventValue,
                eventName = eventName
            )
        )
    }
}