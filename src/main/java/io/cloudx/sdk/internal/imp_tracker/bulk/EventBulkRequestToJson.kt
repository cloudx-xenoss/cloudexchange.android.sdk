package io.cloudx.sdk.internal.imp_tracker.bulk

import org.json.JSONArray
import org.json.JSONObject

internal fun List<EventAM>.toJson(): String {
    val itemsArray = JSONArray()

    for (item in this) {
        val itemObj = JSONObject().apply {
            put("eventName", item.eventName)
            put("campaignId", item.campaignId)
            put("eventValue", item.eventValue)
            put("type", item.type)
            put("impression", item.impression)
        }
        itemsArray.put(itemObj)
    }

    return JSONObject().apply {
        put("items", itemsArray)
    }.toString()
}