package io.cloudx.sdk.internal.startup

import android.content.Context
import androidx.startup.Initializer
import io.cloudx.sdk.internal.appfgduration.AppForegroundDurationService

internal class AppForegroundDurationServiceStartup : Initializer<Unit> {

    override fun create(context: Context) {
        AppForegroundDurationService().start()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf()
    }
}