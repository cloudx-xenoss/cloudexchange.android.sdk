package io.cloudx.sdk.testing

object SdkEnvironment {
    var overridesProvider: SdkOverridesProvider? = null
}

interface SdkOverridesProvider {
    fun getBundleOverride(): String? = null
    fun getIFAOverride(): String? = null
}

