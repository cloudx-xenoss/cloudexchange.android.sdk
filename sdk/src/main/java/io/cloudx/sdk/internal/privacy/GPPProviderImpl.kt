package io.cloudx.sdk.internal.privacy

import android.content.Context
import android.preference.PreferenceManager
import io.cloudx.sdk.internal.ApplicationContext
import io.cloudx.sdk.internal.CloudXLogger

internal interface GPPProvider {
    suspend fun gppString(): String?
    suspend fun gppSid(): List<Int>?
}

internal fun GPPProvider(): GPPProvider = LazySingleInstance

private val LazySingleInstance by lazy {
    GPPProviderImpl(ApplicationContext())
}

private class GPPProviderImpl(context: Context) : GPPProvider {

    @Suppress("DEPRECATION")
    private val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)

    override suspend fun gppString(): String? {
        return try {
            sharedPrefs.getString(IABGPP_GppString, null)?.takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            CloudXLogger.error(TAG, "Failed to read GPP string: ${e.message}")
            null
        }
    }

    override suspend fun gppSid(): List<Int>? {
        return try {
            val raw = sharedPrefs.getString(IABGPP_GppSID, null)?.takeIf { it.isNotBlank() }
            raw
                ?.trim()
                ?.split("_")
                ?.mapNotNull { it.toIntOrNull() }
                ?.takeIf { it.isNotEmpty() }
        } catch (e: Exception) {
            CloudXLogger.error(TAG, "Failed to read or parse GPP SID: ${e.message}")
            null
        }
    }

    companion object {
        private const val TAG = "GPPProviderImpl"
    }
}