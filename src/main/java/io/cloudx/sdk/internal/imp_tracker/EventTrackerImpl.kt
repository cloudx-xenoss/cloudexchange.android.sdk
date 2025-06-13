package io.cloudx.sdk.internal.imp_tracker

import io.cloudx.sdk.internal.Logger
import io.cloudx.sdk.internal.appfgduration.AppForegroundDurationService
import io.cloudx.sdk.internal.db.CloudXDb
import io.cloudx.sdk.internal.db.imp_tracking.CachedTrackingEvents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.UUID

internal class EventTrackerImpl(
    private val scope: CoroutineScope,
    private val appForegroundDurationService: AppForegroundDurationService,
    private val db: CloudXDb
) : EventTracker {

    private val tag = "EventTracker"
    private var impressionEndpoint: String? = null
    private var clickEndpoint: String? = null

    private val trackingApi = TrackingApi()

    override fun setEndpoints(impressionEndpoint: String?, clickEndpoint: String?) {
        this.impressionEndpoint = impressionEndpoint
        this.clickEndpoint = clickEndpoint
    }

    override fun send(
        encoded: String,
        campaignId: String,
        eventValue: Int,
        eventName: String
    ) {
        // fire-and-forget
        scope.launch {
            trackEvent(encoded, campaignId, eventValue, eventName)
        }
    }

    private suspend fun trackEvent(
        encoded: String,
        campaignId: String,
        eventValue: Int,
        eventName: String
    ) {
        val endpoint = endpointFor(eventName)
        val label = eventName.replaceFirstChar { it.uppercase() }

        if (endpoint == null) {
            Logger.e(tag, "No endpoint configured for event='$eventName', caching")
            saveToDb(encoded, campaignId, eventValue, eventName)
            return
        }

        when (val result = trackingApi.send(endpoint, encoded, campaignId, eventValue, eventName)) {
            is io.cloudx.sdk.Result.Success -> {
                Logger.d(tag, "$label sent successfully.")
            }

            else -> {
                Logger.e(tag, "$label failed to send. Caching for retry later.")
                saveToDb(encoded, campaignId, eventValue, eventName)
            }
        }
    }

    override fun trySendingPendingTrackingEvents() {
        scope.launch {
            val cached = db.cachedTrackingEventDao().getAll()
            if (cached.isEmpty()) {
                Logger.d(tag, "No pending tracking events to send")
                return@launch
            }

            Logger.d(tag, "Found ${cached.size} pending tracking events to retry")

            // sequential retries; you can swap to parallel if desired
            cached.forEach { entry ->
                retryEntry(entry)
            }
        }
    }

    private suspend fun retryEntry(entry: CachedTrackingEvents) {
        val endpoint = endpointFor(entry.eventName)
        if (endpoint == null) {
            Logger.e(tag, "No endpoint for '${entry.eventName}', skipping ${entry.id}")
            return
        }

        when (val result = trackingApi.send(
            endpoint,
            entry.encoded,
            entry.campaignId,
            entry.eventValue,
            entry.eventName
        )) {
            is io.cloudx.sdk.Result.Success -> {
                Logger.d(tag, "Successfully resent cached event: ${entry.id}")
                db.cachedTrackingEventDao().delete(entry.id)
            }

            else -> {
                Logger.e(tag, "Retry failed for cached event: ${entry.id}, will keep for later")
            }
        }
    }

    private fun endpointFor(eventName: String): String? =
        when (eventName) {
            "click" -> clickEndpoint
            "imp" -> impressionEndpoint
            else -> null
        }

    private suspend fun saveToDb(
        encoded: String,
        campaignId: String,
        eventValue: Int,
        eventName: String
    ) {
        db.cachedTrackingEventDao().insert(
            CachedTrackingEvents(
                id = UUID.randomUUID().toString(),
                encoded = encoded,
                campaignId = campaignId,
                eventValue = eventValue,
                eventName = eventName
            )
        )
    }
}
