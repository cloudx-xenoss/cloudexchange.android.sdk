package io.cloudx.sdk.internal

import android.util.Log
import io.cloudx.sdk.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.runBlocking

// TODO. Merge with Logger.kt
object CloudXLogger {

    @JvmStatic
    var logEnabled: Boolean = BuildConfig.DEBUG

    private const val TAG = "CloudX"

    private val _logFlow = MutableSharedFlow<LogItem>()
    val logFlow = _logFlow.asSharedFlow()

    fun debug(tag: String = TAG, msg: String, forceLogging: Boolean = false) {
        log(LogItem(LogItem.Type.Debug, tag, msg), forceLogging)
    }

    fun info(tag: String = TAG, msg: String, forceLogging: Boolean = false) {
        log(LogItem(LogItem.Type.Info, tag, msg), forceLogging)
    }

    fun warn(tag: String = TAG, msg: String, forceLogging: Boolean = false) {
        log(LogItem(LogItem.Type.Warn, tag, msg), forceLogging)
    }

    fun error(tag: String = TAG, msg: String, forceLogging: Boolean = false) {
        log(LogItem(LogItem.Type.Error, tag, msg), forceLogging)
    }

    private fun log(logItem: LogItem, forceLogging: Boolean) {
        if (!logEnabled && !forceLogging) {
            return
        }

        with(logItem) {
            when (logItem.type) {
                LogItem.Type.Debug -> Log.d(tag, msg)
                LogItem.Type.Info -> Log.i(tag, msg)
                LogItem.Type.Warn -> Log.w(tag, msg)
                LogItem.Type.Error -> Log.e(tag, msg)
            }
        }

        // TODO. RunBlocking might be not ideal, consider scope with limited parallelism or flow buffer.
        runBlocking(Dispatchers.Main.immediate) {
            _logFlow.emit(logItem)
        }
    }

    data class LogItem(
        val type: Type,
        val tag: String,
        val msg: String
    ) {

        enum class Type {
            Debug, Info, Warn, Error
        }
    }
}