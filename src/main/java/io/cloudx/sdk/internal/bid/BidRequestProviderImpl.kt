package io.cloudx.sdk.internal.bid

import android.content.Context
import io.cloudx.sdk.internal.state.SdkUserState
import io.cloudx.sdk.internal.AdNetwork
import io.cloudx.sdk.internal.AdType
import io.cloudx.sdk.internal.adapter.BidRequestExtrasProvider
import io.cloudx.sdk.internal.appinfo.AppInfoProvider
import io.cloudx.sdk.internal.connectionstatus.ConnectionStatusService
import io.cloudx.sdk.internal.connectionstatus.ConnectionType
import io.cloudx.sdk.internal.deviceinfo.DeviceInfo
import io.cloudx.sdk.internal.deviceinfo.DeviceInfoProvider
import io.cloudx.sdk.internal.gaid.GAIDProvider
import io.cloudx.sdk.internal.geo.GeoInfoHolder
import io.cloudx.sdk.internal.httpclient.UserAgentProvider
import io.cloudx.sdk.internal.lineitem.state.PlacementLoopIndexTracker
import io.cloudx.sdk.internal.location.LocationProvider
import io.cloudx.sdk.internal.nativead.NativeAdSpecs
import io.cloudx.sdk.internal.privacy.PrivacyService
import io.cloudx.sdk.internal.screen.ScreenService
import io.cloudx.sdk.internal.state.SdkKeyValueState
import io.cloudx.sdk.internal.targeting.TargetingService
import io.cloudx.sdk.testing.SdkEnvironment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.Date
import java.util.TimeZone
import java.util.UUID

internal class BidRequestProviderImpl(
    private val context: Context,
    private val sdkVersion: String,
    private val provideAppInfo: AppInfoProvider,
    private val provideDeviceInfo: DeviceInfoProvider,
    private val provideScreenData: ScreenService,
    private val connectionStatusService: ConnectionStatusService,
    private val provideUserAgent: UserAgentProvider,
    private val provideGAID: GAIDProvider,
    private val privacyService: PrivacyService,
    private val targetingService: TargetingService,
    private val locationProvider: LocationProvider,
    private val bidRequestExtrasProviders: Map<AdNetwork, BidRequestExtrasProvider>
) : BidRequestProvider {

    override suspend fun invoke(params: BidRequestProvider.Params): JSONObject =
        withContext(Dispatchers.IO) {
            JSONObject().apply {

                put("id", UUID.randomUUID().toString())

                // These lines are for demo/test purposes
                val bundleOverride = SdkEnvironment.overridesProvider?.getBundleOverride() ?: ""
                val ifaOverride = SdkEnvironment.overridesProvider?.getIFAOverride() ?: ""

                put("app", JSONObject().apply {
                    val appInfo = provideAppInfo()
                    put("id", "5646234")
                    put("bundle", bundleOverride.ifEmpty { appInfo.packageName })
                    put("ver", appInfo.appVersion)
                    put("publisher", JSONObject().apply {
                        put("ext", JSONObject().apply {
                            put("prebid", JSONObject().apply {
                                put("parentAccount", params.accountId)
                            })
                        })
                    })
                })

                val deviceInfo = provideDeviceInfo()

                val adPrivacyData = provideGAID()
                val isDnt = if (adPrivacyData.isLimitAdTrackingEnabled) 1 else 0
                val isLmt = isDnt // Duplication for now.

                val screenData = provideScreenData()

                put("device", JSONObject().apply {

                    put("carrier", deviceInfo.mobileCarrier)
                    put(
                        "connectiontype",
                        connectionStatusService.currentConnectionInfo()?.type.toOrtbConnectionType()
                    )
                    put("devicetype", deviceInfo.toOrtbDeviceType())

                    put("dnt", isDnt)
                    put("lmt", isLmt)
                    put("ifa", ifaOverride.ifEmpty { adPrivacyData.gaid })

                    put("geo", JSONObject().apply {

                        locationProvider()?.let { ld ->
                            put("lat", ld.lat)
                            put("lon", ld.lon)
                            put("accuracy", ld.accuracy)
                            put("type", 1) // 1 - gps/location services source.
                        }
                        put("utcoffset", TimeZone.getDefault().getOffset(Date().time) / 60000 /* ms -> s*/)
                        GeoInfoHolder.getGeoInfo().forEach { (key, value) ->
                            put(key, value)
                        }
                    })

                    put("h", screenData.heightPx)
                    put("w", screenData.widthPx)
                    put("pxratio", screenData.pxRatio)
                    put("ppi", screenData.dpi)

                    put("make", deviceInfo.manufacturer)
                    put("model", deviceInfo.model)
                    put("hwv", deviceInfo.hwVersion)
                    put("os", deviceInfo.os)
                    put("osv", deviceInfo.osVersion)

                    put("ua", provideUserAgent())

                    put("language", deviceInfo.language)

                    put("js", 1)
                })

                put("imp", JSONArray().apply {
                    put(JSONObject().apply {

                        val effectiveAdId = params.withEffectiveAdId()

                        put("id", "1")
                        put("tagid", params.adId)

                        put("secure", 1)

                        val adType = params.adType

                        // TODO. Refactor.
                        val isBannerOrNative = adType is AdType.Banner || adType is AdType.Native
//                        put("instl", if (isBannerOrNative) 0 else 1)

                        if (adType is AdType.Native) {
                            putNativeObject(adType.specs)
                        } else {
                            val adSizeDp = if (isBannerOrNative) {
                                (adType as AdType.Banner).size.let { it.w to it.h }
                            } else {
                                screenData.widthDp to screenData.heightDp
                            }

                            val pos = if (isBannerOrNative) /*UNKNOWN*/ 0 else /*AD_POSITION_FULLSCREEN*/ 7
                            val apis = SupportedOrtbAPIs

                            putBannerObject(apis, adSizeDp, pos)

                            if (!isBannerOrNative) {
                                putVideoObject(apis, adSizeDp, pos)
                            }
                        }

                        put("ext", JSONObject().apply {

                            put("prebid", JSONObject().apply {
                                put("storedimpression", JSONObject().apply {
                                    put("id", effectiveAdId)
                                })

//                                if (SdkKeyValueState.keyValues.isNotEmpty()) {
//                                    put("adservertargeting", JSONArray().apply {
//
//                                        val loopIndex = PlacementLoopIndexTracker.getCount(params.placementName)
//                                        put(JSONObject().apply {
//                                            put("key", "loop-index")
//                                            put("source", "bidrequest")
//                                            put("value", loopIndex.toString())
//                                        })
//
//                                        put(JSONObject().apply {
//                                            put("key", "age")
//                                            put("source", "lambda")
//                                            put("value", "30")
//                                        })
//
//                                        SdkKeyValueState.keyValues.forEach { (k, v) ->
//                                            put(JSONObject().apply {
//                                                put("key", k)
//                                                put("source", "bidrequest")
//                                                put("value", v)
//                                            })
//                                        }
//                                    })
//                                }
                            })

                            SdkKeyValueState.bidderKeyValues.forEach { (bidder, keyValueMap) ->
                                put(bidder, JSONObject().apply {
                                    put("adservertargeting", JSONArray().apply {
                                        keyValueMap.forEach { (k, v) ->
                                            put(JSONObject().apply {
                                                put("key", k)
                                                put("source", "bidrequest")
                                                put("value", v)
                                            })
                                        }
                                    })
                                })
                            }
                        })
                    })
                })

                // hardcoded for now.
//                put("tmax", 1000)
                val hashedUserId = SdkUserState.hashedUserId
                if (!hashedUserId.isNullOrBlank()) {
                    put("user", JSONObject().apply {
                        put("ext", JSONObject().apply {
                            put("prebid", JSONObject().apply {
                                put("buyeruids", JSONObject().apply {
                                        put("cloudx", hashedUserId)

                                    // New: additional hashed key-values
//                                SdkKeyValueState.hashedKeyValues.forEach { (k, v) ->
//                                    put(k, v)
//                                }
                                })
                            })
                        })
                    })
                }

                putRegsObject(privacyService)

                put("ext", JSONObject().apply {
                    put("prebid", JSONObject().apply {
                        if (SdkKeyValueState.keyValues.isNotEmpty()) {
                            put("adservertargeting", JSONArray().apply {

                                val loopIndex = PlacementLoopIndexTracker.getCount(params.placementName)
                                put(JSONObject().apply {
                                    put("key", "loop-index")
                                    put("source", "bidrequest")
                                    put("value", loopIndex.toString())
                                })

                                SdkKeyValueState.keyValues.forEach { (k, v) ->
                                    put(JSONObject().apply {
                                        put("key", k)
                                        put("source", "bidrequest")
                                        put("value", v)
                                    })
                                }
                            })
                        }
                    })
                    putBidRequestAdapterExtras(context, bidRequestExtrasProviders)
                })
            }
        }
}

private suspend fun JSONObject.putRegsObject(privacyService: PrivacyService) {
    put("regs", JSONObject().apply {
        val cloudXPrivacy = privacyService.cloudXPrivacy.value

        put("coppa", cloudXPrivacy.isAgeRestrictedUser.toOrtbRegsValue())

        put("ext", JSONObject().apply {
            put("gdpr_consent", cloudXPrivacy.isUserConsent.toOrtbRegsValue())

            put("ccpa_do_not_sell", cloudXPrivacy.isDoNotSell.toOrtbRegsValue())

            val iabJsonObj = JSONObject().apply {
                put("gdpr_tcfv2_gdpr_applies", privacyService.gdprApplies().toOrtbRegsValue())
                put("gdpr_tcfv2_tc_string", privacyService.tcString())

                put("ccpa_us_privacy_string", privacyService.usPrivacyString())
            }

            if (iabJsonObj.length() > 0) {
                put("iab", iabJsonObj)
            }
        })
    })
}

private fun JSONObject.putBannerObject(apis: JSONArray, adSizeDp: Pair<Int, Int>, pos: Int) {
    put("banner", JSONObject().apply {
//        put("id", "1")

//        put("btype", ExcludedOrtbBannerTypes)
//        put("api", apis)
//        put("mimes", SupportedMimeTypes)

        put("format", JSONArray().apply {
            put(JSONObject().apply {
                put("w", adSizeDp.first)
                put("h", adSizeDp.second)
            })
            put(JSONObject().apply {
                put("w", adSizeDp.first)
                put("h", adSizeDp.second)
            })
        })

//        put("w", adSizeDp.first)
//        put("h", adSizeDp.second)
//
//        put("pos", pos)
    })
}

private fun JSONObject.putVideoObject(apis: JSONArray, adSizeDp: Pair<Int, Int>, pos: Int) {
    put("video", JSONObject().apply {
        put("api", apis)
        put("companiontype", SupportedCompanionTypes)
        put("mimes", SupportedMimeTypes)
        put("protocols", SupportedOrtbProtocols)
        put("placement", /*FLOATING_PLACEMENT*/5)
        put("linearity", /*LINEAR*/ 1)

        put("w", adSizeDp.first)
        put("h", adSizeDp.second)

        put("pos", pos)
    })
}

private fun JSONObject.putNativeObject(specs: NativeAdSpecs) {
    val ver = NativeVer

    val requestField = JSONObject().apply {
        put("ver", ver)

        // Always 1.
        put("privacy", 1)

        put(
            "eventtrackers",
            JSONArray().apply {
                specs.eventTrackers.forEach {
                    put(
                        JSONObject().apply {
                            put("event", it.event.value)
                            put("methods", JSONArray(it.methodTypes.map { it.value }.toList()))
                        }
                    )
                }
            }
        )

        put(
            "assets",
            JSONArray().apply {
                specs.assets.values.forEach {
                    put(
                        JSONObject().apply {
                            put("id", it.id)
                            put("required", it.required.toInt())

                            when (it) {
                                is NativeAdSpecs.Asset.Data -> put(
                                    "data",
                                    JSONObject().apply {
                                        put("type", it.type.value)
                                        it.len?.let { len -> put("len", len) }
                                    }
                                )

                                is NativeAdSpecs.Asset.Image -> put(
                                    "img",
                                    JSONObject().apply {
                                        put("type", it.type.value)
                                        // Allow any sizes.
                                        put("wmin", 1)
                                        put("hmin", 1)
                                    }
                                )

                                is NativeAdSpecs.Asset.Title -> put(
                                    "title",
                                    JSONObject().apply {
                                        put("len", it.length)
                                    }
                                )

                                is NativeAdSpecs.Asset.Video -> put(
                                    "video",
                                    JSONObject().apply {
                                        // Currently using hardcoded ones.
                                        put("mimes", SupportedMimeTypes)
                                        put("protocols", SupportedOrtbProtocols)
                                    }
                                )
                            }
                        }
                    )
                }
            }
        )
    }

    put(
        "native",
        JSONObject().apply {
            put("ver", ver)
            put("request", requestField.toString())
        }
    )
}

private const val NativeVer = "1.2"

private fun ConnectionType?.toOrtbConnectionType(): Int = when (this) {
    ConnectionType.Ethernet -> 1
    ConnectionType.WIFI -> 2
    ConnectionType.MobileUnknown -> 3
    ConnectionType.Mobile2g -> 4
    ConnectionType.Mobile3g -> 5
    ConnectionType.Mobile4g -> 6
    // TODO. 5g const check;
    // ConnectionType.Mobile5g -> 7
    else -> 0 // null or connection_unknown
}

private fun DeviceInfo.toOrtbDeviceType(): Int = if (isTablet) 5 else 1 // mobile

private fun Boolean.toInt(): Int = if (this) 1 else 0

private fun Boolean?.toOrtbRegsValue(): Any = when (this) {
    true -> 1
    false -> 0
    null -> JSONObject.NULL
}

private val SupportedCompanionTypes = JSONArray().apply {
    // STATIC
    put(1)
    // HTML
    put(2)
}

private val SupportedMimeTypes = JSONArray().apply {
    put("video/mp4")
    put("video/3gpp")
    put("video/3gpp2")
    put("video/x-m4v")
    // TODO. Remove quicktime?
    put("video/quicktime")
}

private val SupportedOrtbProtocols = JSONArray().apply {
    // Vast 2-4 + Vast Wrapper support.
    put(2)
    put(3)
    put(4)
    put(5)
    put(6)
    put(7)
}

private val SupportedOrtbAPIs = JSONArray().apply {
    // MRAID_1
    put(3)
    // MRAID_2
    put(5)
    // MRAID_3
    put(6)
    // OMID_1
    put(7)
}

private val ExcludedOrtbBannerTypes = JSONArray().apply {
    // XHTML_TEXT_AD
    put(1)
    // IFRAME
    put(4)
}