package io.cloudx.sdk.internal.kil_switch

import android.content.Context
import android.content.SharedPreferences

internal class KillSwitchServiceImpl(context: Context) : KillSwitchService {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("cloudx_prefs", Context.MODE_PRIVATE)

    override fun newSession() {
        clear()
    }

    /**
     * Called during SDK initialization.
     * Always rerolls decision for a new session.
     */
    override fun decideOnInit(ratio: Double?) {
        if (ratio == null) return

        println("KillSwitchServiceImpl decideOnInit: ratio=$ratio")

        val random = Math.random() // [0.0, 1.0)
        val turnedOn = random < ratio

        println("KillSwitchServiceImpl decideOnInit: random=$random, turnedOn=$turnedOn")

        prefs.edit()
            .putBoolean(KEY_TURNED_ON, turnedOn)
            .putString(KEY_RATIO, ratio.toString())
            .apply()
    }

    /**
     * Called during bid response.
     * Reuses init decision if ratio unchanged,
     * reevaluates if server ratio differs.
     */
    override fun updateOnBid(ratio: Double?) {
        if (ratio == null) return

        val lastRatio = prefs.getString(KEY_RATIO, null)?.toDoubleOrNull()
        if (lastRatio != null && lastRatio == ratio) {
            // ratio unchanged → stick with current state
            return
        }

        val random = Math.random()
        val turnedOn = random > ratio

        println("KillSwitchServiceImpl updateOnBid: ratio=$ratio, random=$random, turnedOn=$turnedOn")

        prefs.edit()
            .putBoolean(KEY_TURNED_ON, turnedOn)
            .putString(KEY_RATIO, ratio.toString())
            .apply()
    }

    override fun isTurnedOn(): Boolean {
        return prefs.getBoolean(KEY_TURNED_ON, false)
    }

    private fun clear() {
        prefs.edit()
            .remove(KEY_TURNED_ON)
            .remove(KEY_RATIO)
            .apply()
    }

    companion object {
        private const val KEY_TURNED_ON = "kill_switch_enabled"
        private const val KEY_RATIO = "kill_switch_ratio"
    }
}

