package io.cloudx.sdk.internal.core.ad.suspendable

import io.cloudx.sdk.internal.AdNetwork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// TODO. Some methods/inits can be reused for any ad type (destroy() etc).
// TODO. Replace sdk.adapter.RewardedInterstitial with this?
// TODO. Merge with DecoratedSuspendableXXXX?
internal interface SuspendableRewardedInterstitial : SuspendableBaseFullscreenAd<SuspendableRewardedInterstitialEvent>

sealed class SuspendableRewardedInterstitialEvent {
    object Load : SuspendableRewardedInterstitialEvent()
    object Show : SuspendableRewardedInterstitialEvent()
    object Impression : SuspendableRewardedInterstitialEvent()
    object Reward : SuspendableRewardedInterstitialEvent()
    object Hide : SuspendableRewardedInterstitialEvent()
    object Click : SuspendableRewardedInterstitialEvent()

    class Error(val error: io.cloudx.sdk.internal.adapter.CloudXAdError) :
        SuspendableRewardedInterstitialEvent()
}

internal fun SuspendableRewardedInterstitial(
    price: Double?,
    adNetwork: AdNetwork,
    adUnitId: String,
    createRewardedInterstitial: (listener: io.cloudx.sdk.internal.adapter.RewardedInterstitialListener) -> io.cloudx.sdk.internal.adapter.RewardedInterstitial
): SuspendableRewardedInterstitial =
    SuspendableRewardedInterstitialImpl(
        price,
        adNetwork,
        adUnitId,
        createRewardedInterstitial
    )

private class SuspendableRewardedInterstitialImpl(
    override val price: Double?,
    override val adNetwork: AdNetwork,
    override val adUnitId: String,
    createRewardedInterstitial: (listener: io.cloudx.sdk.internal.adapter.RewardedInterstitialListener) -> io.cloudx.sdk.internal.adapter.RewardedInterstitial,
) : SuspendableRewardedInterstitial {

    private val scope = CoroutineScope(Dispatchers.Main)

    private val rewardedInterstitial =
        createRewardedInterstitial(object :
            io.cloudx.sdk.internal.adapter.RewardedInterstitialListener {
            override fun onLoad() {
                scope.launch { _event.emit(SuspendableRewardedInterstitialEvent.Load) }
            }

            override fun onShow() {
                scope.launch { _event.emit(SuspendableRewardedInterstitialEvent.Show) }
            }

            override fun onImpression() {
                scope.launch { _event.emit(SuspendableRewardedInterstitialEvent.Impression) }
            }

            override fun onEligibleForReward() {
                scope.launch { _event.emit(SuspendableRewardedInterstitialEvent.Reward) }
            }

            override fun onHide() {
                scope.launch { _event.emit(SuspendableRewardedInterstitialEvent.Hide) }
            }

            override fun onClick() {
                scope.launch { _event.emit(SuspendableRewardedInterstitialEvent.Click) }
            }

            override fun onError(error: io.cloudx.sdk.internal.adapter.CloudXAdError) {
                scope.launch {
                    _event.emit(SuspendableRewardedInterstitialEvent.Error(error))
                    // 1 liner instead of _event.collect { /*assign error*/ }
                    _lastErrorEvent.value = error
                }
            }
        })

    override val isAdLoadOperationAvailable: Boolean
        get() = rewardedInterstitial.isAdLoadOperationAvailable

    override suspend fun load(): Boolean {
        val evtJob = scope.async {
            event.first {
                it is SuspendableRewardedInterstitialEvent.Load || it is SuspendableRewardedInterstitialEvent.Error
            }
        }

        rewardedInterstitial.load()

        return evtJob.await() is SuspendableRewardedInterstitialEvent.Load
    }

    override fun timeout() {
        // unused
    }

    override fun show() {
        rewardedInterstitial.show()
    }

    private val _event = MutableSharedFlow<SuspendableRewardedInterstitialEvent>()
    override val event: SharedFlow<SuspendableRewardedInterstitialEvent> = _event

    private val _lastErrorEvent =
        MutableStateFlow<io.cloudx.sdk.internal.adapter.CloudXAdError?>(null)
    override val lastErrorEvent: StateFlow<io.cloudx.sdk.internal.adapter.CloudXAdError?> =
        _lastErrorEvent

    override fun destroy() {
        scope.cancel()
        rewardedInterstitial.destroy()
    }
}