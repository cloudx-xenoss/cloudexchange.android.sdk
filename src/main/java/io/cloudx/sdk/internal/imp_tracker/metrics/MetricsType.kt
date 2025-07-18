package io.cloudx.sdk.internal.imp_tracker.metrics

enum class MetricsType(
    val typeCode: String
) {
    SDK_INIT("method_sdk_init"),
    GEO_API("method_geo_api"),
    BID_REQUEST("method_bid_request"),
}