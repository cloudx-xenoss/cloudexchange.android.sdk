package io.cloudx.sdk.internal

/**
 * List of supported Ad networks.
 */
internal sealed class AdNetwork {

    data object TestNetwork : AdNetwork()
    data object GoogleAdManager : AdNetwork()
    data object Meta : AdNetwork()
    data object Mintegral : AdNetwork()
    data object CloudX : AdNetwork()
    data object CloudXSecond : AdNetwork()
    data class Unknown(val name: String) : AdNetwork()
}