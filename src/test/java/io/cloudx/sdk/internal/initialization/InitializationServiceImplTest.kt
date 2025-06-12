package io.cloudx.sdk.internal.initialization

import io.cloudx.sdk.Result
import io.cloudx.sdk.RoboMockkTest
import io.cloudx.sdk.internal.appinfo.AppInfoProvider
import io.cloudx.sdk.internal.deviceinfo.DeviceInfoProvider
import io.cloudx.sdk.internal.geo.GeoApi
import io.cloudx.sdk.internal.imp_tracker.ImpressionTracker
import io.cloudx.sdk.internal.privacy.PrivacyService
import io.cloudx.sdk.internal.tracking.MetricsTracker
import io.cloudx.sdk.mocks.MockConfigAPIWithPredefinedConfig
import io.cloudx.sdk.mocks.MockConfigRequestProviderWithArbitraryValues
import io.cloudx.sdk.mocks.MockAdapterFactoryResolver
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class InitializationServiceImplTest : RoboMockkTest() {

    // TODO: More tests.

    @Test
    fun serviceInitializedWhenConfigAPIRequestSuccessful() = runTest {
        val initializationService = InitializationServiceImpl(
            configApi = MockConfigAPIWithPredefinedConfig(),
            provideConfigRequest = MockConfigRequestProviderWithArbitraryValues(),
            adapterResolver = MockAdapterFactoryResolver(),
            privacyService = PrivacyService(),
            metricsTracker = MetricsTracker(),
            provideAppInfo = AppInfoProvider(),
            provideDeviceInfo = DeviceInfoProvider(),
            impressionTracker = ImpressionTracker(),
            geoApi = GeoApi()
        )

        val result = initializationService.initialize("random_app_key", activity = mockk())

        assert(result is Result.Success) {
            "expected Success result with config provided, got $result"
        }

        assert(initializationService.initialized) {
            "expected initialized == true after getting successful config, got false"
        }
    }
}