package io.cloudx.sdk.internal.state

internal object SdkKeyValueState {

    val keyValues: MutableMap<String, String> = mutableMapOf()

    val hashedKeyValues: MutableMap<String, String> = mutableMapOf()

    val bidderKeyValues: MutableMap<String, MutableMap<String, String>> = mutableMapOf()

    fun clear() {
        keyValues.clear()
        hashedKeyValues.clear()
        bidderKeyValues.clear()
    }
}
