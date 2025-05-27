package io.cloudx.sdk.internal.adapterrepo

import io.cloudx.sdk.internal.AdNetwork

/**
 * Hub for retrieving runtime adapters (FB, AdMob banner/int/rew adapters etc)
 */
internal interface AdapterRepository {

    suspend fun adNetworkInitializer(adNetwork: AdNetwork): Any?
    suspend fun bannerFactory(adNetwork: AdNetwork): Any?
    suspend fun interstitialFactory(adNetwork: AdNetwork): Any?
    suspend fun rewardedFactory(adNetwork: AdNetwork): Any?
}

internal fun AdapterRepository(): AdapterRepository = LazySingleInstance

private val LazySingleInstance by lazy { AdapterRepositoryImpl() }