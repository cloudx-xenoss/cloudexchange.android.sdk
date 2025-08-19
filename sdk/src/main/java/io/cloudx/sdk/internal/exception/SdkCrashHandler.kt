package io.cloudx.sdk.internal.exception

internal class SdkCrashHandler(
    private val reportCrash: (Thread, Throwable) -> Unit
) : Thread.UncaughtExceptionHandler {

    private val previousHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        reportCrash(thread, throwable)
        previousHandler?.uncaughtException(thread, throwable)
    }
}
