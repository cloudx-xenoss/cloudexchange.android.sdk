package io.cloudx.sdk.internal.privacy

import io.cloudx.sdk.CloudXPrivacy
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Facade Privacy API: contains publisher defined flag based privacy data, IAB based APIs (us string, tcf string) as well.
 */
internal interface PrivacyService : TCFProvider, USPrivacyProvider, GPPProvider {

    /**
     * Holds privacy data set explicitly by publishers (COPPA, GDPR consent, Do Not Sell values etc),
     * which are supposed to be used when there's no better alternative available
     * such as TCF TC string (GDPR) or US Privacy String (CCPA)
     */
    val cloudXPrivacy: MutableStateFlow<CloudXPrivacy>
}

internal fun PrivacyService(): PrivacyService = LazySingleInstance

private val LazySingleInstance by lazy {
    PrivacyServiceImpl(
        tcfProvider = TCFProvider(),
        usPrivacyProvider = USPrivacyProvider(),
        gppProvider = GPPProvider()
    )
}