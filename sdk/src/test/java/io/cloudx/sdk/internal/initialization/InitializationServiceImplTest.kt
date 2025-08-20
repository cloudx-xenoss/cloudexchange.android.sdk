package io.cloudx.sdk.internal.initialization

import android.app.Activity
import android.content.SharedPreferences
import io.cloudx.sdk.Result
import io.cloudx.sdk.RoboMockkTest
import io.cloudx.sdk.internal.appinfo.AppInfoProvider
import io.cloudx.sdk.internal.deviceinfo.DeviceInfoProvider
import io.cloudx.sdk.internal.geo.GeoApi
import io.cloudx.sdk.internal.imp_tracker.EventTracker
import io.cloudx.sdk.internal.imp_tracker.metrics.MetricsTrackerNew
import io.cloudx.sdk.internal.privacy.PrivacyService
import io.cloudx.sdk.internal.tracking.MetricsTracker
import io.cloudx.sdk.mocks.MockConfigAPIWithPredefinedConfig
import io.cloudx.sdk.mocks.MockConfigRequestProviderWithArbitraryValues
import io.cloudx.sdk.mocks.MockAdapterFactoryResolver
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class InitializationServiceImplTest : RoboMockkTest() {

    // TODO: More tests.

    @Test
    fun serviceInitializedWhenConfigAPIRequestSuccessful() = runTest {
        val mockPrefs = mockk<SharedPreferences> {
            every { getString("pending_crash", any()) } returns null // or return valid JSON
        }

        val activity = mockk<Activity> {
            every { getSharedPreferences("cloudx_crash_store", any()) } returns mockPrefs
        }

        val initializationService = InitializationServiceImpl(
            configApi = MockConfigAPIWithPredefinedConfig(),
            provideConfigRequest = MockConfigRequestProviderWithArbitraryValues(),
            adapterResolver = MockAdapterFactoryResolver(),
            privacyService = PrivacyService(),
            metricsTracker = MetricsTracker(),
            _metricsTrackerNew = MetricsTrackerNew(),
            provideAppInfo = AppInfoProvider(),
            provideDeviceInfo = DeviceInfoProvider(),
            eventTracker = EventTracker(),
            geoApi = GeoApi(),
            context = activity.applicationContext
        )

        val result = initializationService.initialize("random_app_key")

        assert(result is Result.Success) {
            "expected Success result with config provided, got $result"
        }

        assert(initializationService.initialized) {
            "expected initialized == true after getting successful config, got false"
        }
    }
}