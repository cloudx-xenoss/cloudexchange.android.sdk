package io.cloudx.sdk.internal.location

import io.cloudx.sdk.internal.ApplicationContext

internal interface LocationProvider {

    /**
     * Provides currently available location data
     *
     * @return null if location data unavailable.
     */
    suspend operator fun invoke(): Location?

    class Location(val lat: Float, val lon: Float, val accuracy: Float)
}

internal fun LocationProvider(): LocationProvider = LazySingleInstance

private val LazySingleInstance by lazy {
    GoogleFusedLocationProvider(
        ApplicationContext()
    )
}