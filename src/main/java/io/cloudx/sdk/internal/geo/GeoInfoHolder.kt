package io.cloudx.sdk.internal.geo

object GeoInfoHolder {

    private const val KEY_COUNTRY = "cloudfront-viewer-country-iso3"
    private const val COUNTRY_USA = "USA"

    @Volatile
    private var geoInfo: Map<String, String>? = null

    fun setGeoInfo(info: Map<String, String>) {
        geoInfo = info
    }

    fun getGeoInfo(): Map<String, String> =
        geoInfo ?: emptyMap()

    fun isUSUser(): Boolean {
        return getGeoInfo()[KEY_COUNTRY]?.lowercase() == COUNTRY_USA.lowercase()
    }

    fun clear() {
        geoInfo = null
    }
}
