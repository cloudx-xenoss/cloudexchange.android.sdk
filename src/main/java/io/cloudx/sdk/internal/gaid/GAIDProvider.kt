package io.cloudx.sdk.internal.gaid

import io.cloudx.sdk.internal.ApplicationContext

internal interface GAIDProvider {

    suspend operator fun invoke(): Result

    /**
     * @property gaid - Google Advertising Id.
     * @property isLimitAdTrackingEnabled - Do Not track
     * @constructor Create empty Result
     */
    class Result(
        val gaid: String,
        val isLimitAdTrackingEnabled: Boolean
    )
}

internal fun GAIDProvider(): GAIDProvider = LazySingleInstance

private val LazySingleInstance: GAIDProvider by lazy {
    GAIDProviderImpl(
        ApplicationContext()
    )
}