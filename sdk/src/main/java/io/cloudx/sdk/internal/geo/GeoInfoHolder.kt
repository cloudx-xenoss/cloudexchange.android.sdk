package io.cloudx.sdk.internal.geo

object GeoInfoHolder {
    @Volatile
    private var geoInfo: Map<String, String>? = null

    fun setGeoInfo(info: Map<String, String>) {
        geoInfo = info
    }

    fun getGeoInfo(): Map<String, String> =
        geoInfo ?: emptyMap()

    fun clear() {
        geoInfo = null
    }
}
