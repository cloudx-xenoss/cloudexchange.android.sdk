package io.cloudx.sdk.internal.config

import io.cloudx.sdk.internal.appinfo.AppInfoProvider
import io.cloudx.sdk.internal.deviceinfo.DeviceInfoProvider
import io.cloudx.sdk.internal.gaid.GAIDProvider

internal fun interface ConfigRequestProvider {

    suspend operator fun invoke(): ConfigRequest
}

internal fun ConfigRequestProvider(): ConfigRequestProvider = LazySingleInstance

private val LazySingleInstance by lazy {
    ConfigRequestProviderImpl(
        io.cloudx.sdk.BuildConfig.SDK_VERSION_NAME,
        AppInfoProvider(),
        DeviceInfoProvider(),
        GAIDProvider()
    )
}