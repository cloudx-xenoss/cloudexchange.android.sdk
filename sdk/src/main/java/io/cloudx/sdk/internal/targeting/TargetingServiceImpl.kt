package io.cloudx.sdk.internal.targeting

import io.cloudx.sdk.CloudXTargeting
import kotlinx.coroutines.flow.MutableStateFlow

internal class TargetingServiceImpl(
    override val cloudXTargeting: MutableStateFlow<CloudXTargeting?> = MutableStateFlow(null)
) : TargetingService