package io.cloudx.adapter.mintegral

internal fun Map<String, String>.placementId(): String? = get("placement_id")
internal fun Map<String, String>.bidId(): String? = get("bid_id")