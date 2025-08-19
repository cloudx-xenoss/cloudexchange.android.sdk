package io.cloudx.sdk.internal.geo

object GeoInfoHolder {

    private const val KEY_REGION = "cloudfront-viewer-country-region"
    private const val KEY_COUNTRY = "cloudfront-viewer-country-iso3"
    private const val REGION_CALIFORNIA = "CA"
    private const val COUNTRY_USA = "USA"

    @Volatile
    private var processedGeoInfo: Map<String, String>? = null
    private var rawGeoInfo: Map<String, String> = emptyMap()

    fun setGeoInfo(processedGeoInfo: Map<String, String>, rawGeoInfo: Map<String, String>) {
        this.processedGeoInfo = processedGeoInfo
        this.rawGeoInfo = rawGeoInfo
    }

    fun getGeoInfo(): Map<String, String> =
        processedGeoInfo ?: emptyMap()

    fun isUSUser(): Boolean {
        return rawGeoInfo[KEY_COUNTRY]?.lowercase() == COUNTRY_USA.lowercase()
    }

    fun isCaliforniaUser(): Boolean {
        if (isUSUser()) {
            return rawGeoInfo[KEY_REGION]?.lowercase() == REGION_CALIFORNIA.lowercase()
        }
        return false
    }

    fun clear() {
        processedGeoInfo = null
    }
}
