package io.cloudx.sdk.internal.bid

import io.cloudx.sdk.CloudXTargeting
import io.cloudx.sdk.internal.targeting.TargetingService
import org.json.JSONObject

internal fun JSONObject.putUserTargeting(targetingService: TargetingService) {
    val targeting = targetingService.cloudXTargeting.value ?: return

    // "id" : null Exchange-specific ID for the user. null for now
    // Not passed yet.

    // "buyer_id" : null
    // Not passed yet.

    with(targeting) {
        yob?.let {
            put("yob", it)
        }

        gender?.let {
            put("gender", it.toOrtbValue())
        }

        if (!keywords.isNullOrEmpty()) {
            put("keywords", keywords.toCommaSeparatedString())
        }

        if (!data.isNullOrEmpty()) {
            put("data", data.toJsonObject())
        }

        put("ext", JSONObject().apply {
            age?.let {
                put("age", it)
            }

            userID?.let {
                put("publisherUserID", it)
            }
        })
    }
}

private fun CloudXTargeting.Gender.toOrtbValue() = when (this) {
    CloudXTargeting.Gender.Male -> "M"
    CloudXTargeting.Gender.Female -> "F"
    CloudXTargeting.Gender.Other -> "O"
}

private fun List<String>.toCommaSeparatedString(): String = reduce { el1, el2 ->
    "$el1,$el2,"
}.trimEnd(',')

private fun Map<String, String>.toJsonObject(): JSONObject = JSONObject().apply {
    entries.onEach {
        put(it.key, it.value)
    }
}