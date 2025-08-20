package io.cloudx.sdk.internal.bid

import io.cloudx.sdk.internal.AdNetwork

internal data class NoBidResponse(val id: String, val noBidResponseCode: Int, val ext: String?)

internal class BidResponse(
    val auctionId: String,
    val seatBid: List<SeatBid>,
    val killSwitchRatio: Double?
)

internal class SeatBid(
    val bid: List<Bid>,
)

internal class Bid(
    val id: String,
    val adm: String,
    val price: Float?,
    val priceRaw: String?,
    val burl: String?,
    val nurl: String?,
    val lurl: String?,
    val adNetwork: AdNetwork,
    /**
     * Bid rank across other bids within [BidResponse], can be used sorting.
     */
    val rank: Int,
    /**
     * auth keys and other data required for ad network's adapter to load and show ads properly.
     */
    val adapterExtras: Map<String, String>,
    val dealId: String?,
    val creativeId: String?,
    val auctionId: String,
    val adWidth: Int?,
    val adHeight: Int?,
)