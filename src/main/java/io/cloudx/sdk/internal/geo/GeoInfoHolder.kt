package io.cloudx.sdk.internal.geo

object GeoInfoHolder {

    private const val KEY_REGION = "cloudfront-viewer-country-region"
    private const val KEY_COUNTRY = "cloudfront-viewer-country-iso3"
    private const val REGION_CALIFORNIA = "CA"
    private const val COUNTRY_USA = "USA"

    @Volatile
    private var geoInfo: Map<String, String>? = null

    fun setGeoInfo(info: Map<String, String>) {
        geoInfo = info
    }

    fun getGeoInfo(): Map<String, String> =
        geoInfo ?: emptyMap()

    fun isCaliforniaUser(): Boolean {
        val isUs = getGeoInfo()[KEY_COUNTRY]?.lowercase() == COUNTRY_USA.lowercase()
        val region = getGeoInfo()[KEY_REGION]?.lowercase()
        return region == REGION_CALIFORNIA.lowercase() && isUs
    }

    fun clear() {
        geoInfo = null
    }
}
