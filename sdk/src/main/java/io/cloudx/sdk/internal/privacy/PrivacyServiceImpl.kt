package io.cloudx.sdk.internal.privacy

import io.cloudx.sdk.CloudXPrivacy
import io.cloudx.sdk.internal.geo.GeoInfoHolder
import kotlinx.coroutines.flow.MutableStateFlow

internal class PrivacyServiceImpl(
    override val cloudXPrivacy: MutableStateFlow<CloudXPrivacy> = MutableStateFlow(CloudXPrivacy()),
    tcfProvider: TCFProvider,
    usPrivacyProvider: USPrivacyProvider,
    val gppProvider: GPPProvider
) : PrivacyService, TCFProvider by tcfProvider, USPrivacyProvider by usPrivacyProvider,
    GPPProvider by gppProvider {

    override fun shouldClearPersonalData(): Boolean {
        val isUSUser = GeoInfoHolder.isUSUser()
        if (!isUSUser) {
            return false // Non US users do not require personal data clearing
        }

        // US user
        val isCoppa = isCoppaEnabled()
        if (isCoppa) {
            return true // COPPA users always require personal data clearing within the US
        }

        val isCaliforniaUser = GeoInfoHolder.isCaliforniaUser()
        val gppConsent = if (isCaliforniaUser) {
            decodeGpp(GppTarget.US_CA)
        } else {
            decodeGpp(GppTarget.US_NATIONAL)
        }
        val clear = gppConsent?.requiresPiiRemoval() == true
        return clear
    }

    override fun isCoppaEnabled(): Boolean {
        return cloudXPrivacy.value.isAgeRestrictedUser == true
    }
}