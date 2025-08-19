package io.cloudx.sdk.internal

import android.content.Context

/**
 * Application context holder. Important to initialize it prior to any other component/service.
 *
 * @param context
 * @return
 */
internal fun ApplicationContext(context: Context? = null): Context {
    context?.let {
        applicationContext = it.applicationContext
    }

    return applicationContext
}

@Volatile
private lateinit var applicationContext: Context