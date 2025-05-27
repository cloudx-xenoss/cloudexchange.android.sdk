package io.cloudx.sdk.internal.config

import io.cloudx.sdk.internal.appinfo.AppInfoProvider
import io.cloudx.sdk.internal.deviceinfo.DeviceInfoProvider
import io.cloudx.sdk.internal.gaid.GAIDProvider

internal class ConfigRequestProviderImpl(
    private val sdkVersion: String,
    private val provideAppInfo: AppInfoProvider,
    private val provideDeviceInfo: DeviceInfoProvider,
    private val provideGAID: GAIDProvider
) : ConfigRequestProvider {

    override suspend fun invoke(): ConfigRequest {
        val deviceInfo = provideDeviceInfo()
        val gaidData = provideGAID()

        return ConfigRequest(
            bundle = provideAppInfo().packageName,
            os = deviceInfo.os,
            osVersion = deviceInfo.osVersion,
            deviceModel = deviceInfo.model,
            deviceManufacturer = deviceInfo.manufacturer,
            sdkVersion = sdkVersion,
            gaid = gaidData.gaid,
            dnt = gaidData.isLimitAdTrackingEnabled
        )
    }
}