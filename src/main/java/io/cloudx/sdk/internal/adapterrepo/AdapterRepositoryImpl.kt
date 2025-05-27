package io.cloudx.sdk.internal.adapterrepo

import io.cloudx.sdk.internal.AdNetwork
import io.cloudx.sdk.internal.objectInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Adapter repository:
 *
 * lazily attempts finding respective [AdNetwork] adapter instance; returns null otherwise.
 *
 * _Current implementation allows only 1 caller max: other callers can't enter even if they call APIs then the current caller.
 */
internal class AdapterRepositoryImpl : AdapterRepository {

    private val adNetworkInitializers = mutableMapOf<AdNetwork, Any>()

    override suspend fun adNetworkInitializer(adNetwork: AdNetwork): Any? = withLockOnDispatcherIO {
        adNetworkInitializers.getOrTryCreate(adNetwork)
    }

    private val bannerFactories = mutableMapOf<AdNetwork, Any>()

    override suspend fun bannerFactory(adNetwork: AdNetwork): Any? = withLockOnDispatcherIO {
        bannerFactories.getOrTryCreate(adNetwork)
    }

    private val interstitialFactories = mutableMapOf<AdNetwork, Any>()

    override suspend fun interstitialFactory(adNetwork: AdNetwork): Any? = withLockOnDispatcherIO {
        interstitialFactories.getOrTryCreate(adNetwork)
    }

    private val rewardedFactories = mutableMapOf<AdNetwork, Any>()

    override suspend fun rewardedFactory(adNetwork: AdNetwork): Any? = withLockOnDispatcherIO {
        rewardedFactories.getOrTryCreate(adNetwork)
    }

    // Allows 1 API caller at time for now.
    private val mutex = Mutex()

    private suspend fun <T> withLockOnDispatcherIO(action: () -> T?): T? = mutex.withLock {
        withContext(Dispatchers.IO) {
            action()
        }
    }
}

private inline fun <reified T> MutableMap<AdNetwork, T>.getOrTryCreate(adNetwork: AdNetwork): T? {
    val cachedInstance = this[adNetwork]
    if (cachedInstance != null) {
        return cachedInstance
    }

    val resolvedInstance = objectInstance<T>("io.cloudx.sdk.TODO")
    if (resolvedInstance != null) {
        this[adNetwork] = resolvedInstance
    }

    return resolvedInstance
}