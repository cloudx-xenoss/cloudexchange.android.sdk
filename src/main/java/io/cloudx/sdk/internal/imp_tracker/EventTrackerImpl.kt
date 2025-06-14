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
        encoded: String, campaignId: String, eventValue: Int, eventType: EventType
    ) {
        scope.launch {
            trackEvent(encoded, campaignId, eventValue, eventType)
        }
    }

    private suspend fun trackEvent(
        encoded: String, campaignId: String, eventValue: Int, eventType: EventType
    ) {
        val endpoint = when (eventType) {
            EventType.Click -> clickEndpoint
            EventType.Impression -> impressionEndpoint
        }

        if (endpoint.isNullOrBlank()) {
            Logger.e(tag, "No endpoint for $eventType, caching event")
            saveToDb(encoded, campaignId, eventValue, eventType)
            return
        }

        val result = trackingApi.send(
            endpoint, encoded, campaignId, eventValue, eventType.code
        )
        if (result is io.cloudx.sdk.Result.Success) {
            Logger.d(tag, "$eventType sent successfully.")
        } else {
            Logger.e(tag, "$eventType failed to send. Caching for retry later.")
            saveToDb(encoded, campaignId, eventValue, eventType)
        }
    }

    override fun trySendingPendingTrackingEvents() {
        scope.launch {
            val cached = db.cachedTrackingEventDao().getAll()
            if (cached.isEmpty()) {
                Logger.d(tag, "No pending tracking events to send")
                return@launch
            }
            Logger.d(tag, "Found ${cached.size} pending events to retry")
            cached.forEach { retryEntry(it) }
        }
    }

    private suspend fun retryEntry(entry: CachedTrackingEvents) {
        EventType.from(entry.eventName)?.let { eventType ->
            val endpoint = when (eventType) {
                EventType.Click -> clickEndpoint
                EventType.Impression -> impressionEndpoint
            }
            Logger.d(tag, "retryEntry: $eventType → endpoint=$endpoint for id=${entry.id}")
            if (endpoint.isNullOrBlank()) {
                Logger.e(tag, "No endpoint for $eventType, skipping ${entry.id}")
                return
            }
            val result = trackingApi.send(
                endpoint, entry.encoded, entry.campaignId, entry.eventValue, eventType.code
            )
            if (result is io.cloudx.sdk.Result.Success) {
                Logger.d(tag, "Resend success for ${entry.id}")
                db.cachedTrackingEventDao().delete(entry.id)
            } else {
                Logger.e(tag, "Resend failed for ${entry.id}, keeping for later")
            }
        } ?: run {
            Logger.e(tag, "Unknown eventName='${entry.eventName}', deleting ${entry.id}")
            db.cachedTrackingEventDao().delete(entry.id)
        }
    }

    private suspend fun saveToDb(
        encoded: String, campaignId: String, eventValue: Int, eventType: EventType
    ) {
        db.cachedTrackingEventDao().insert(
            CachedTrackingEvents(
                id = UUID.randomUUID().toString(),
                encoded = encoded,
                campaignId = campaignId,
                eventValue = eventValue,
                eventName = eventType.code
            )
        )
    }
}
