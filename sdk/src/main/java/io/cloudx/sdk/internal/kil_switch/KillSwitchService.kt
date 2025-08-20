package io.cloudx.sdk.internal.kil_switch

import android.content.Context

internal class KillSwitchService(context: Context) {
    private val prefs = context.getSharedPreferences("cloudx_prefs", Context.MODE_PRIVATE)
    private val KEY = "kill_switch_enabled"

    fun isEnabled(): Boolean = prefs.getBoolean(KEY, false)

    fun setEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY, enabled).apply()
    }

    fun updateFromServerFlag(serverFlag: Boolean) {
        setEnabled(serverFlag)
    }

    fun clear() {
        prefs.edit().remove(KEY).apply()
    }
}
