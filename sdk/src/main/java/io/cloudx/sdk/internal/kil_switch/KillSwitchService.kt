package io.cloudx.sdk.internal.kil_switch

import io.cloudx.sdk.internal.ApplicationContext

internal interface KillSwitchService {

    fun newSession()

    /**
     * Called during SDK initialization.
     * Always rerolls decision for the new session.
     * @return the evaluated enabled/disabled state
     */
    fun decideOnInit(ratio: Double?)

    /**
     * Called during bid response.
     * Reuses init decision if ratio unchanged,
     * reevaluates if ratio differs.
     * @return the evaluated enabled/disabled state
     */
    fun updateOnBid(ratio: Double?)

    fun isTurnedOn(): Boolean
}

internal fun KillSwitchService(): KillSwitchService = LazySingleInstance

private val LazySingleInstance by lazy {
    KillSwitchServiceImpl(ApplicationContext())
}