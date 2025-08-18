package io.cloudx.sdk.internal

// TODO. Merge with CloudXLogger.
/**
 * CloudX Logger: all SDK logging should go here.
 */
internal object Logger {

    /**
     * Info log level
     */
    fun i(tag: String = TAG, msg: String) {
        CloudXLogger.info(tag, msg)
    }

    /**
     * Debug log level
     */
    fun d(tag: String = TAG, msg: String) {
        CloudXLogger.debug(tag, msg)
    }

    /**
     * Warning log level
     */
    fun w(tag: String = TAG, msg: String) {
        CloudXLogger.warn(tag, msg)
    }

    /**
     * Error log level
     */
    fun e(tag: String = TAG, msg: String?, tr: Throwable? = null) {
        CloudXLogger.error(tag, msg ?: "")
    }
}

private const val TAG = "CloudX"