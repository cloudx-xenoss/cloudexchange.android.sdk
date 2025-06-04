package io.cloudx.sdk.internal.initialization

import android.app.Activity
import io.cloudx.sdk.internal.Error
import io.cloudx.sdk.Result
import io.cloudx.sdk.internal.adfactory.AdFactory
import io.cloudx.sdk.internal.appinfo.AppInfoProvider
import io.cloudx.sdk.internal.config.Config
import io.cloudx.sdk.internal.config.ConfigApi
import io.cloudx.sdk.internal.config.ConfigRequestProvider
import io.cloudx.sdk.internal.core.resolver.AdapterFactoryResolver
import io.cloudx.sdk.internal.deviceinfo.DeviceInfoProvider
import io.cloudx.sdk.internal.geo.GeoApi
import io.cloudx.sdk.internal.imp_tracker.ImpressionTracker
import io.cloudx.sdk.internal.imp_tracker.ImpressionTrackingApi
import io.cloudx.sdk.internal.privacy.PrivacyService
import io.cloudx.sdk.internal.tracking.MetricsTracker

/**
 * Initialization service - responsible for all CloudX initialization related things, notably - configuration fetching.
 */
internal interface InitializationService {

    /**
     * Initialized status of CloudX SDK
     */
    val initialized: Boolean

    /**
     * Initialize CloudX SDK
     * @param appKey - unique application key/identifier; comes from app's Publisher.
     * @return [Config] upon successful initialization, [Error] otherwise
     */
    suspend fun initialize(appKey: String, activity: Activity): Result<Config, Error>

    /**
     * Ad factory - null when SDK is not [initialized]
     */
    val adFactory: AdFactory?
}

internal fun InitializationService(
    configApi: ConfigApi,
    provideConfigRequest: ConfigRequestProvider = ConfigRequestProvider(),
    adapterFactoryResolver: AdapterFactoryResolver = AdapterFactoryResolver(),
    privacyService: PrivacyService = PrivacyService(),
    metricsTracker: MetricsTracker = MetricsTracker(),
    impressionTracker: ImpressionTracker = ImpressionTracker(),
    appInfoProvider: AppInfoProvider = AppInfoProvider(),
    deviceInfoProvider: DeviceInfoProvider = DeviceInfoProvider(),
    geoApi: GeoApi = GeoApi()
): InitializationService =
    InitializationServiceImpl(
        configApi,
        provideConfigRequest,
        adapterFactoryResolver,
        privacyService,
        metricsTracker,
        impressionTracker,
        appInfoProvider,
        deviceInfoProvider,
        geoApi
    )