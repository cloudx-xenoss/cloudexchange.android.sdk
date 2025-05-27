package io.cloudx.sdk

interface CloudXAdToDisplayInfoApi {

    /**
     * Ad to display info of the first ad in a queue to display
     */
    val adToDisplayInfo: Info?

    /**
     * Info
     *
     * @property publisherRevenue revenue value of the ad if available; null - otherwise
     * @property networkName network name of the adapter responsible for ad loading/displaying.
     */
    data class Info(val publisherRevenue: Double?, val networkName: String)
}