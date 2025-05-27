package io.cloudx.sdk.internal.privacy

import io.cloudx.sdk.CloudXPrivacy
import kotlinx.coroutines.flow.MutableStateFlow

internal class PrivacyServiceImpl(
    override val cloudXPrivacy: MutableStateFlow<CloudXPrivacy> = MutableStateFlow(CloudXPrivacy()),
    tcfProvider: TCFProvider,
    usPrivacyProvider: USPrivacyProvider
) : PrivacyService, TCFProvider by tcfProvider, USPrivacyProvider by usPrivacyProvider