package io.cloudx.sdk.internal.bid

import io.cloudx.sdk.internal.core.ad.suspendable.SuspendableBanner

internal data class LoadResult(val banner: SuspendableBanner?, val lossReason: LossReason?)
