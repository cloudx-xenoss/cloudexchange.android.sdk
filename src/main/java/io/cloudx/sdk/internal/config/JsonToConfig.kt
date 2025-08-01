package io.cloudx.sdk.internal.config

import io.cloudx.sdk.Result
import io.cloudx.sdk.internal.AdNetwork
import io.cloudx.sdk.internal.Error
import io.cloudx.sdk.internal.Logger
import io.cloudx.sdk.internal.toAdNetwork
import io.cloudx.sdk.internal.toStringPairMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

internal suspend fun jsonToConfig(json: String): Result<Config, Error> =
    withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(json)

            val auctionEndpoint = root.toEndpointConfig("auctionEndpointURL")
            val cdpEndpoint = root.toEndpointConfig("cdpEndpointURL")

            Result.Success(
                Config(
                    precacheSize = root.getInt("preCacheSize"),
                    auctionEndpointUrl = auctionEndpoint,
                    cdpEndpointUrl = cdpEndpoint,
                    eventTrackingEndpointUrl = root.getString("eventTrackingURL"),
                    trackingEndpointUrl = root.optString("impressionTrackerURL", null),
                    bidders = root.getJSONArray("bidders").toBidders(),
                    placements = root.getJSONArray("placements").toPlacements(),
                    metricsEndpointUrl = root.getString("metricsEndpointURL"),
                    geoDataEndpointUrl = root.optString("geoDataEndpointURL", null),
                    sessionId = root.getString("sessionID") + UUID.randomUUID().toString(),
                    organizationId = root.optString("organizationID", null),
                    accountId = root.optString("accountID", null),
                    appKeyOverride = root.optString("appKeyOverride", null),
                    trackers = root.optJSONArray("tracking")?.toTrackers(),
                    geoHeaders = root.optJSONArray("geoHeaders")?.toGeoHeaders(),
                    keyValuePaths = root.optJSONObject("keyValuePaths")?.let { kvp ->
                        Config.KeyValuePaths(
                            userKeyValues = kvp.optString("userKeyValues", null),
                            appKeyValues = kvp.optString("appKeyValues", null),
                            eids = kvp.optString("eids", null),
                            placementLoopIndex = kvp.optString("placementLoopIndex", null)
                        )
                    },
                    metrics = root.optJSONObject("metrics")?.toMetricsConfig(),
                    rawJson = root
                )
            )
        } catch (e: Exception) {
            val errStr = e.toString()
            Logger.e(tag = "jsonToConfig", msg = errStr)

            Result.Failure(Error(errStr))
        }
    }

private fun JSONArray.toBidders(): Map<AdNetwork, Config.Bidder> {
    val bidders = mutableMapOf<AdNetwork, Config.Bidder>()
    val length = length()

    for (i in 0 until length) {
        val bidder = getJSONObject(i)

        val adNetwork = bidder.getString("networkName").toAdNetwork()

        bidders[adNetwork] = Config.Bidder(
            adNetwork = adNetwork,
            initData = bidder.getJSONObject("initData").toStringPairMap()
        )
    }

    return bidders
}

private fun JSONArray.toTrackers(): List<String> {
    val params = mutableListOf<String>()
    val length = length()

    for (i in 0 until length) {
        val param = getString(i)
        params.add(param)
    }
    return params
}

private fun JSONArray.toGeoHeaders(): List<Config.GeoHeader> {
    val headers = mutableListOf<Config.GeoHeader>()
    for (i in 0 until length()) {
        val obj = getJSONObject(i)
        val source = obj.optString("source")
        val target = obj.optString("target")
        if (source.isNotEmpty() && target.isNotEmpty()) {
            headers.add(Config.GeoHeader(source, target))
        }
    }
    return headers
}

private fun JSONObject.toMetricsConfig(): Config.MetricsConfig {
    return Config.MetricsConfig(
        sendIntervalSeconds = this.optLong("send_interval_seconds", 60),
        sdkApiCallsEnabled = if (has("sdk_api_calls.enabled")) optBoolean("sdk_api_calls.enabled") else null,
        networkCallsEnabled = if (has("network_calls.enabled")) optBoolean("network_calls.enabled") else null,
        networkCallsBidReqEnabled = if (has("network_calls.bid_req.enabled")) optBoolean("network_calls.bid_req.enabled") else null,
        networkCallsInitSdkReqEnabled = if (has("network_calls.init_sdk_req.enabled")) optBoolean("network_calls.init_sdk_req.enabled") else null,
        networkCallsGeoReqEnabled = if (has("network_calls.geo_req.enabled")) optBoolean("network_calls.geo_req.enabled") else null
    )
}

private fun JSONArray.toPlacements(): Map<String, Config.Placement> {
    val placements = mutableMapOf<String, Config.Placement>()
    val length = length()

    for (i in 0 until length) {
        val jsonPlacement = getJSONObject(i)

        val id = jsonPlacement.getString("id")
        val name = jsonPlacement.getString("name")
        val bidResponseTimeoutMillis = jsonPlacement.getInt("bidResponseTimeoutMs")
        val adLoadTimeoutMillis = jsonPlacement.getInt("adLoadTimeoutMs")
        val placementType = jsonPlacement.getString("type")
        val hasCloseButton = jsonPlacement.opt("hasCloseButton") as? Boolean ?: false
        val lineItems = jsonPlacement.toLineItems()

        val placement = when (placementType.uppercase()) {

            "BANNER" -> Config.Placement.Banner(
                id,
                name,
                bidResponseTimeoutMillis,
                adLoadTimeoutMillis,
                refreshRateMillis = jsonPlacement.getInt("bannerRefreshRateMs"),
                hasCloseButton,
                lineItems
            )

            "MREC" -> Config.Placement.MREC(
                id,
                name,
                bidResponseTimeoutMillis,
                adLoadTimeoutMillis,
                refreshRateMillis = jsonPlacement.getInt("bannerRefreshRateMs"),
                hasCloseButton,
                lineItems
            )

            "INTERSTITIAL" -> Config.Placement.Interstitial(
                id, name, bidResponseTimeoutMillis, adLoadTimeoutMillis, lineItems
            )

            "REWARDED" -> Config.Placement.Rewarded(
                id, name, bidResponseTimeoutMillis, adLoadTimeoutMillis, lineItems
            )

            "NATIVE" -> Config.Placement.Native(
                id,
                name,
                bidResponseTimeoutMillis,
                adLoadTimeoutMillis,
                jsonPlacement.toNativeTemplateType(),
                // TODO. getInt() once back-end supports.
                refreshRateMillis = jsonPlacement.optInt("bannerRefreshRateMs", 900_000),
                hasCloseButton,
                lineItems
            )

            else -> {
                Logger.w("JSONArray.toPlacements()", "unknown placement type: $placementType")
                null
            }
        }

        if (placement != null) {
            placements[name] = placement
        }
    }

    return placements
}

private fun JSONObject.toNativeTemplateType(): Config.Placement.Native.TemplateType =
    when (val templateString = getString("nativeTemplate")) {
        "small" -> Config.Placement.Native.TemplateType.Small
        "medium" -> Config.Placement.Native.TemplateType.Medium
        else -> Config.Placement.Native.TemplateType.Unknown(templateString)
    }

private fun JSONObject.toLineItems(): List<Config.LineItem> {
    val lineItems = mutableListOf<Config.LineItem>()
    val array = optJSONArray("line_items") ?: return lineItems

    for (i in 0 until array.length()) {
        val itemObj = array.getJSONObject(i)

        val suffix = itemObj.optString("suffix", null)
        val targetingObj = itemObj.optJSONObject("targeting")

        val targeting = targetingObj?.let {
            val strategy = it.optString("strategy", "Default")
            val conditionsAnd = it.optBoolean("conditionsAnd", false)
            val conditionsArray = it.optJSONArray("conditions") ?: JSONArray()

            val conditions = mutableListOf<Config.LineItem.Condition>()
            for (j in 0 until conditionsArray.length()) {
                val conditionObj = conditionsArray.getJSONObject(j)

                val whitelist =
                    conditionObj.optJSONArray("whitelist")?.toFilterList() ?: emptyList()
                val blacklist =
                    conditionObj.optJSONArray("blacklist")?.toFilterList() ?: emptyList()
                val and = conditionObj.optBoolean("and", false)

                conditions += Config.LineItem.Condition(whitelist, blacklist, and)
            }

            Config.LineItem.Targeting(strategy, conditionsAnd, conditions)
        }

        lineItems += Config.LineItem(suffix = suffix, targeting = targeting)
    }

    return lineItems
}

private fun JSONArray.toFilterList(): List<Map<String, Any>> {
    val list = mutableListOf<Map<String, Any>>()
    for (i in 0 until length()) {
        val obj = getJSONObject(i)
        val map = mutableMapOf<String, Any>()
        obj.keys().forEach { key -> map[key] = obj[key] }
        list += map
    }
    return list
}

internal fun JSONObject.toEndpointConfig(field: String): Config.EndpointConfig {
    val raw = opt(field)
    return when (raw) {
        is String -> Config.EndpointConfig(default = raw)
        is JSONObject -> {
            val testArray = raw.optJSONArray("test") ?: JSONArray()
            val test = mutableListOf<Config.EndpointConfig.TestVariant>()

            for (i in 0 until testArray.length()) {
                val obj = testArray.getJSONObject(i)
                val name = obj.getString("name")
                val url = obj.getString("value")
                val ratio = obj.optDouble("ratio", 1.0)
                test += Config.EndpointConfig.TestVariant(name, url, ratio)
            }

            Config.EndpointConfig(
                default = raw.getString("default"),
                test = test
            )
        }

        else -> Config.EndpointConfig(default = "")
    }
}


