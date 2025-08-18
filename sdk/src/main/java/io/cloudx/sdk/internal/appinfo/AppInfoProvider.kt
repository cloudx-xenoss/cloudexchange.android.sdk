package io.cloudx.sdk.internal.appinfo

import io.cloudx.sdk.internal.ApplicationContext

internal fun interface AppInfoProvider {

    suspend operator fun invoke(): AppInfo
}

internal class AppInfo(val appName: String, val packageName: String, val appVersion: String)

internal fun AppInfoProvider(): AppInfoProvider = LazySingleInstance

private val LazySingleInstance by lazy {
    AppInfoProviderImpl(
        ApplicationContext()
    )
}