package io.cloudx.sdk.internal.bid

import android.app.Activity
import io.cloudx.sdk.BuildConfig
import io.cloudx.sdk.internal.AdNetwork
import io.cloudx.sdk.internal.AdType
import io.cloudx.sdk.internal.CloudXLogger
import io.cloudx.sdk.internal.adapter.BidRequestExtrasProvider
import io.cloudx.sdk.internal.appinfo.AppInfoProvider
import io.cloudx.sdk.internal.config.Config
import io.cloudx.sdk.internal.connectionstatus.ConnectionStatusService
import io.cloudx.sdk.internal.deviceinfo.DeviceInfoProvider
import io.cloudx.sdk.internal.gaid.GAIDProvider
import io.cloudx.sdk.internal.httpclient.UserAgentProvider
import io.cloudx.sdk.internal.lineitem.LineItemEvaluator
import io.cloudx.sdk.internal.lineitem.MatchContext
import io.cloudx.sdk.internal.location.LocationProvider
import io.cloudx.sdk.internal.privacy.PrivacyService
import io.cloudx.sdk.internal.screen.ScreenService
import io.cloudx.sdk.internal.targeting.TargetingService
import io.cloudx.sdk.internal.lineitem.state.PlacementLoopIndexTracker
import io.cloudx.sdk.internal.state.SdkKeyValueState
import org.json.JSONObject

// TODO. Separate Json conversion logic from business logic.
internal interface BidRequestProvider {

    suspend fun invoke(params: Params, auctionId: String): JSONObject

    class Params(
        val adId: String,
        val adType: AdType,
        val placementName: String,
        val lineItems: List<Config.LineItem>?,
        val accountId: String,
        val appKey: String
    )
}

internal fun BidRequestProvider.Params.withEffectiveAdId(): String {
    if (placementName.isBlank()) {
        println("❌ withEffectiveAdId: adId=$adId (no placementName)")
    }

    val currentLoopIndex = if (adType is AdType.Interstitial || adType is AdType.Rewarded) {
        0
    } else {
        PlacementLoopIndexTracker.getAndIncrement(placementName)
    }

    return adId
}


internal fun BidRequestProvider(
    activity: Activity,
    bidRequestExtrasProviders: Map<AdNetwork, BidRequestExtrasProvider>
) = BidRequestProviderImpl(
    activity.applicationContext,
    BuildConfig.SDK_VERSION_NAME,
    AppInfoProvider(),
    DeviceInfoProvider(),
    ScreenService(activity),
    ConnectionStatusService(),
    UserAgentProvider(),
    GAIDProvider(),
    PrivacyService(),
    TargetingService(),
    LocationProvider(),
    bidRequestExtrasProviders
)