package io.cloudx.sdk.internal.adapter

// TODO. Refactor. It shouldn't be implemented by AdFactory:
//  it should belong to Interstitial/whatever adapter.
interface MetaData {

    val sdkVersion: String
}

fun MetaData(sdkVersion: String): MetaData = MetaDataImpl(sdkVersion)

private class MetaDataImpl(override val sdkVersion: String) : MetaData