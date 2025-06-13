package io.cloudx.sdk.internal.initialization

import android.app.Activity
import io.cloudx.sdk.BuildConfig
import io.cloudx.sdk.Result
import io.cloudx.sdk.internal.CloudXLogger
import io.cloudx.sdk.internal.Error
import io.cloudx.sdk.internal.adfactory.AdFactory
import io.cloudx.sdk.internal.appinfo.AppInfoProvider
import io.cloudx.sdk.internal.common.service.ActivityLifecycleService
import io.cloudx.sdk.internal.common.service.AppLifecycleService
import io.cloudx.sdk.internal.common.utcNowEpochMillis
import io.cloudx.sdk.internal.config.Config
import io.cloudx.sdk.internal.config.ConfigApi
import io.cloudx.sdk.internal.config.ConfigRequestProvider
import io.cloudx.sdk.internal.config.ResolvedEndpoints
import io.cloudx.sdk.internal.connectionstatus.ConnectionStatusService
import io.cloudx.sdk.internal.core.resolver.AdapterFactoryResolver
import io.cloudx.sdk.internal.core.resolver.BidAdNetworkFactories
import io.cloudx.sdk.internal.deviceinfo.DeviceInfoProvider
import io.cloudx.sdk.internal.geo.GeoApi
import io.cloudx.sdk.internal.geo.GeoInfoHolder
import io.cloudx.sdk.internal.imp_tracker.EventTracker
import io.cloudx.sdk.internal.imp_tracker.dynamic.TrackingFieldResolver
import io.cloudx.sdk.internal.lineitem.matcher.MatcherRegistry
import io.cloudx.sdk.internal.privacy.PrivacyService
import io.cloudx.sdk.internal.tracking.AdEventApi
import io.cloudx.sdk.internal.tracking.InitOperationStatus
import io.cloudx.sdk.internal.tracking.MetricsTracker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.system.measureTimeMillis

/**
 * Initialization service impl - initializes CloudX SDK; ignores all the following init calls after successful initialization.
 */
internal class InitializationServiceImpl(
    private val configApi: ConfigApi,
    private val provideConfigRequest: ConfigRequestProvider,
    private val adapterResolver: AdapterFactoryResolver,
    private val privacyService: PrivacyService,
    private val metricsTracker: MetricsTracker,
    private val eventTracker: EventTracker,
    private val provideAppInfo: AppInfoProvider,
    private val provideDeviceInfo: DeviceInfoProvider,
    private val geoApi: GeoApi
) : InitializationService {

    override val initialized: Boolean
        get() = config != null

    private var config: Config? = null

    private val mutex = Mutex()

    override suspend fun initialize(appKey: String, activity: Activity): Result<Config, Error> =
        mutex.withLock {
            // It is currently agreed to send pending metrics upon init API invocation.
            metricsTracker.trySendPendingMetrics()

            val config = this.config
            if (config != null) {
                return Result.Success(config)
            }

            val configApiResult: Result<Config, Error>
            val configRequestStartedAtMillis = utcNowEpochMillis()
            val configApiRequestMillis = measureTimeMillis {
                configApiResult = configApi.invoke(appKey, provideConfigRequest())
            }

            if (configApiResult is Result.Success) {
                val cfg = configApiResult.value
                this.config = cfg

                eventTracker.setEndpoints(cfg.impressionTrackerURL, cfg.clickTrackerURL)
                eventTracker.trySendingPendingTrackingEvents()

                ResolvedEndpoints.resolveFrom(cfg)

                metricsTracker.init(appKey, cfg)

                val geoDataResult = geoApi.fetchGeoHeaders(ResolvedEndpoints.geoEndpoint)
                if (geoDataResult is Result.Success) {
                    val headersMap = geoDataResult.value

                    val geoInfo: Map<String, String> = cfg.geoHeaders?.mapNotNull { header ->
                        val sourceHeader = header.source
                        val targetField = header.target
                        val value = headersMap[sourceHeader]

                        value?.let {
                            targetField to it
                        }
                    }?.toMap() ?: emptyMap()

                    CloudXLogger.info("MainActivity", "geo data: $geoInfo")
                    GeoInfoHolder.setGeoInfo(geoInfo)
                }

                val factories = resolveAdapters(cfg)

                val appKeyOverride = cfg.appKeyOverride ?: appKey
                initAdFactory(appKeyOverride, cfg, factories)
                initializeAdapterNetworks(cfg, activity)

                MatcherRegistry.registerMatchers()

                val deviceInfo = provideDeviceInfo()
                val sdkVersion = BuildConfig.SDK_VERSION_NAME
                val deviceType = if (deviceInfo.isTablet) "table" else "mobile"
                val sessionId = cfg.sessionId + UUID.randomUUID().toString()

                TrackingFieldResolver.setSessionConstData(
                    sessionId,
                    sdkVersion,
                    deviceType,
                    ResolvedEndpoints.testGroupName
                )
                TrackingFieldResolver.setConfig(cfg)
            }

            metricsTracker.initOperationStatus(
                InitOperationStatus(
                    success = configApiResult is Result.Success,
                    startedAtUnixMillis = configRequestStartedAtMillis,
                    endedAtUnixMillis = configRequestStartedAtMillis + configApiRequestMillis,
                    appKey = appKey,
                    sessionId = this.config?.sessionId
                )
            )

            configApiResult
        }

    override fun deinitialize() {
        ResolvedEndpoints.reset()
        config = null
        factories = null
        adFactory = null
    }

    private var factories: BidAdNetworkFactories? = null
    override var adFactory: AdFactory? = null
        private set

    // TODO. Replace with LazyAdapterResolver
    private suspend fun resolveAdapters(config: Config): BidAdNetworkFactories =
        withContext(Dispatchers.IO) {
            val factories = adapterResolver.resolveBidAdNetworkFactories(
                forTheseNetworks = config.bidders.map {
                    it.key
                }.toSet()
            )

            this@InitializationServiceImpl.factories = factories
            factories
        }

    private fun initAdFactory(appKey: String, config: Config, factories: BidAdNetworkFactories) {
        adFactory = AdFactory(
            appKey,
            config,
            factories,
            AdEventApi(config.eventTrackingEndpointUrl),
            metricsTracker,
            eventTracker,
            ConnectionStatusService(),
            AppLifecycleService(),
            ActivityLifecycleService()
        )
    }

    private suspend fun initializeAdapterNetworks(config: Config, activity: Activity) {
        val adapterInitializers = factories?.initializers ?: return

        // Initialize only available adapters and if init config is present for them.

        config.bidders.onEach { bidderCfg ->

            val initializer = adapterInitializers[bidderCfg.key]

            if (initializer == null) {
                CloudXLogger.warn(
                    "InitializationServiceImpl",
                    "No initializer found for ${bidderCfg.key}"
                )
                return@onEach
            }

            adapterInitializers[bidderCfg.key]?.initialize(
                activity,
                bidderCfg.value.initData,
                privacyService.cloudXPrivacy
            )
        }
    }
}