package io.cloudx.sdk.internal.imp_tracker.model

import java.util.concurrent.ConcurrentHashMap

internal object ImpressionDataStore {

    private var localData: LocalData? = null
    private val requestDataMap = ConcurrentHashMap<String, RequestData>()
    private val bidMetaMap = ConcurrentHashMap<String, MutableList<BidMetadata>>()
    private val loadedBidMap = ConcurrentHashMap<String, String>() // adId → bidId

    fun saveLocalData(data: LocalData) {
        localData = data
    }

    fun saveRequestData(auctionId: String, data: RequestData) {
        requestDataMap[auctionId] = data
    }

    fun saveResponseData(auctionId: String, metadata: List<BidMetadata>) {
        bidMetaMap[auctionId] = metadata.toMutableList()
    }

    fun saveLoadedBid(auctionId: String, bidId: String) {
        loadedBidMap[auctionId] = bidId
    }

    fun buildImpressionId(auctionId: String): ImpressionId? {
        val local = localData ?: return null
        val request = requestDataMap[auctionId] ?: return null
        val loadedBidId = loadedBidMap[auctionId] ?: return null
        val bid = bidMetaMap[auctionId]?.firstOrNull { it.bidId == loadedBidId } ?: return null

        return ImpressionId(
            bidder = bid.bidder,
            width = bid.width,
            height = bid.height,
            dealId = bid.dealId,
            creativeId = bid.creativeId,
            cpmMicros = bid.priceMicros,
            responseTimeMillis = bid.responseTimeMillis,
            releaseVersion = local.releaseVersion,
            auctionId = request.auctionId,
            accountId = local.accountId,
            organizationId = local.organizationId,
            applicationId = local.applicationId,
            placementId = request.placementId,
            deviceName = local.deviceName,
            deviceType = local.deviceType,
            osName = local.osName,
            osVersion = local.osVersion,
            sessionId = local.sessionId,
            ifa = request.ifa,
            loopIndex = request.loopIndex,
            testGroupName = local.testGroupName
        )
    }

    fun clear(auctionId: String) {
        requestDataMap.remove(auctionId)
        bidMetaMap.remove(auctionId)
        loadedBidMap.remove(auctionId)
    }
}

