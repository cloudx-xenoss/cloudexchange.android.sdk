package io.cloudx.sdk.internal.targeting

import io.cloudx.sdk.CloudXTargeting
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Facade targeting API: contains publisher defined user targeting data.
 */
internal interface TargetingService {

    /**
     * Holds targeting data set explicitly by publishers. Can be used for bid requests, analytics etc
     */
    val cloudXTargeting: MutableStateFlow<CloudXTargeting?>
}

internal fun TargetingService(): TargetingService = LazySingleInstance

private val LazySingleInstance by lazy {
    TargetingServiceImpl()
}