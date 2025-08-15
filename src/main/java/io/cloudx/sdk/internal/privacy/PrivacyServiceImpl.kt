package io.cloudx.sdk.internal.privacy

import io.cloudx.sdk.CloudXPrivacy
import io.cloudx.sdk.internal.geo.GeoInfoHolder
import kotlinx.coroutines.flow.MutableStateFlow

internal class PrivacyServiceImpl(
    override val cloudXPrivacy: MutableStateFlow<CloudXPrivacy> = MutableStateFlow(CloudXPrivacy()),
    tcfProvider: TCFProvider,
    usPrivacyProvider: USPrivacyProvider,
    val gppProvider: GPPProvider
) : PrivacyService,
    TCFProvider by tcfProvider,
    USPrivacyProvider by usPrivacyProvider,
    GPPProvider by gppProvider {

    override fun shouldClearPersonalData(): Boolean {
        val isCoppa = isCoppaEnabled()
        val isCaliforniaUser = GeoInfoHolder.isCaliforniaUser()
        val ccpa = if (isCaliforniaUser) decodedCcpa() else null

        val shouldClear = isCoppa || ccpa?.requiresPiiRemoval() == true
        println("hop: shouldClearPersonalData = $shouldClear (COPPA=$isCoppa, CA=$isCaliforniaUser)")
        return shouldClear
    }

    override fun isCoppaEnabled(): Boolean {
        return cloudXPrivacy.value.isAgeRestrictedUser == true
    }
}