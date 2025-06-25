package io.cloudx.sdk.internal.state

import io.cloudx.sdk.internal.config.Config

internal object SdkKeyValueState {

    val keyValues: MutableMap<String, String> = mutableMapOf()

    val hashedKeyValues: MutableMap<String, String> = mutableMapOf()

    val bidderKeyValues: MutableMap<String, MutableMap<String, String>> = mutableMapOf()

    private var configPaths: Config.KeyValuePaths? = null

    fun clear() {
        keyValues.clear()
        hashedKeyValues.clear()
        bidderKeyValues.clear()
    }

    fun setKeyValuePaths(configPaths: Config.KeyValuePaths?) {
        this.configPaths = configPaths
    }

    fun getKeyValuePaths(): Config.KeyValuePaths? {
        return configPaths
    }
}
