package io.cloudx.sdk.internal

// Logger implementation to work with pure JUnit tests.
/**
 * CloudX Logger: all SDK logging should go here.
 */
internal object Logger {

    /**
     * Info log level
     */
    fun i(tag: String = TAG, msg: String) {
        println("i: =$tag= $msg")
    }

    /**
     * Debug log level
     */
    fun d(tag: String = TAG, msg: String) {
        println("d: =$tag= $msg")
    }

    /**
     * Warning log level
     */
    fun w(tag: String = TAG, msg: String) {
        println("w: =$tag= $msg")
    }

    /**
     * Error log level
     */
    fun e(tag: String = TAG, msg: String?, tr: Throwable? = null) {
        println("e: =$tag= $msg $tr")
    }
}

private const val TAG = "CloudX"