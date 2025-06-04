package io.cloudx.sdk.internal.config

import io.cloudx.sdk.internal.AdNetwork
import io.cloudx.sdk.internal.config.Config.Placement
import org.json.JSONObject

/**
 * Config required for SDK initialization/startup: result of SDK Init API response.
 *
 * @property precacheSize - Max size of precached ad queue per placement
 * @property bidders - bid network's adapters participating in auction and ad precaching within CloudX SDK mediation.
 * @property placements - (key - [Placement.name]) ad placements
 */
internal class Config(
    val sessionId: String,
    val precacheSize: Int,
    val auctionEndpointUrl: EndpointConfig,
    val cdpEndpointUrl: EndpointConfig,
    val eventTrackingEndpointUrl: String,
    val impressionTrackerURL: String?,
    val metricsEndpointUrl: String,
    val geoDataEndpointURL: String,
    val organizationId: String?,
    val accountId: String?,
    val bidders: Map<AdNetwork, Bidder>,
    val placements: Map<String, Placement>,
    val trackers: List<String>?,
    val geoHeaders: List<GeoHeader>?,
    val rawJson: JSONObject?
) {

    /**
     * Bidder
     *
     * @property adNetwork - bid network name.
     * @property initData - bid network SDK's specific data required for its initializing
     */
    class Bidder(
        val adNetwork: AdNetwork,
        val initData: Map<String, String>
    )

    /**
     * Placement data.
     * @property bidResponseTimeoutMillis
     * @property adLoadTimeoutMillis - timeout for ad load operation AFTER bid response returned
     */
    sealed class Placement(
        val id: String,
        val name: String,
        val bidResponseTimeoutMillis: Int,
        val adLoadTimeoutMillis: Int,
        val lineItems: List<LineItem>? = null
    ) {

        open class Banner(
            id: String,
            name: String,
            bidResponseTimeoutMillis: Int,
            adLoadTimeoutMillis: Int,
            val refreshRateMillis: Int,
            val hasCloseButton: Boolean = false,
            lineItems: List<LineItem>? = null
        ) : Placement(
            id,
            name,
            bidResponseTimeoutMillis,
            adLoadTimeoutMillis,
            lineItems
        )

        class MREC(
            id: String,
            name: String,
            bidResponseTimeoutMillis: Int,
            adLoadTimeoutMillis: Int,
            refreshRateMillis: Int,
            hasCloseButton: Boolean = false,
            lineItems: List<LineItem>? = null
        ) : Banner(
            id,
            name,
            bidResponseTimeoutMillis,
            adLoadTimeoutMillis,
            refreshRateMillis,
            hasCloseButton,
            lineItems
        )

        class Interstitial(
            id: String,
            name: String,
            bidResponseTimeoutMillis: Int,
            adLoadTimeoutMillis: Int,
            lineItems: List<LineItem>? = null
        ) : Placement(id, name, bidResponseTimeoutMillis, adLoadTimeoutMillis, lineItems)

        class Rewarded(
            id: String,
            name: String,
            bidResponseTimeoutMillis: Int,
            adLoadTimeoutMillis: Int,
            lineItems: List<LineItem>? = null
        ) : Placement(id, name, bidResponseTimeoutMillis, adLoadTimeoutMillis, lineItems)

        class Native(
            id: String,
            name: String,
            bidResponseTimeoutMillis: Int,
            adLoadTimeoutMillis: Int,
            val templateType: TemplateType,
            val refreshRateMillis: Int,
            val hasCloseButton: Boolean = false,
            lineItems: List<LineItem>? = null
        ) : Placement(
            id,
            name,
            bidResponseTimeoutMillis,
            adLoadTimeoutMillis,
            lineItems
        ) {

            sealed class TemplateType {
                data object Small : TemplateType()
                data object Medium : TemplateType()

                data class Unknown(val name: String) : TemplateType()
            }
        }
    }

    data class LineItem(
        val suffix: String? = null,
        val targeting: Targeting?
    ) {

        data class Targeting(
            val strategy: String,
            val conditionsAnd: Boolean,
            val conditions: List<Condition>
        )

        data class Condition(
            val whitelist: List<Map<String, Any>> = emptyList(),
            val blacklist: List<Map<String, Any>> = emptyList(),
            val and: Boolean
        )
    }

    data class EndpointConfig(
        val default: String,
        val test: List<TestVariant>? = null
    ) {
        data class TestVariant(
            val name: String,
            val value: String,
            val ratio: Double
        )
    }

    data class GeoHeader(
        val source: String,
        val target: String
    )
}