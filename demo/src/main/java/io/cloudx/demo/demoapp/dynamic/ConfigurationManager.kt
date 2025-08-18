package io.cloudx.demo.demoapp.dynamic

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import io.cloudx.demo.demoapp.R
import io.cloudx.sdk.CloudX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConfigurationManager(private val context: Context) {

    private val configService = RetrofitClient.configService
    private val prefs: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(context)
    private var currentConfig: AppConfig? = null

    suspend fun fetchAndApplyRemoteConfig(appKey: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val configFileName = "$appKey.json"
            val response = configService.getAppConfig(configFileName)
            if (response.isSuccessful && response.body() != null) {
                currentConfig = response.body()!!
                saveToPreferences(currentConfig)
                Log.d(
                    "ConfigurationManager",
                    "✅ Remote config loaded and applied for appKey=$appKey"
                )

                val sdkConfig = getCurrentConfig()?.SDKConfiguration
                val userKeyValues = sdkConfig?.userKeyValues
                val appKeyValues = sdkConfig?.appKeyValues

                CloudX.clearAllKeyValues()

                userKeyValues?.forEach { (key, value) ->
                    CloudX.setUserKeyValue(key, value)
                }

                appKeyValues?.forEach { (key, value) ->
                    CloudX.setAppKeyValue(key, value)
                }

                true
            } else {
                Log.w(
                    "ConfigurationManager",
                    "⚠️ No config found for appKey=$appKey (code=${response.code()})"
                )
                false
            }
        } catch (e: Exception) {
            Log.e("ConfigurationManager", "❌ Error fetching remote config", e)
            false
        }
    }

    fun getCurrentConfig(): AppConfig? {
        return currentConfig
    }

    private fun saveToPreferences(config: AppConfig?) {
        if (config == null) return
        prefs.edit {
            // Sdk init url
            val location = config.SDKConfiguration.location
            val urlType = location.type

            putString(
                context.getString(R.string.pref_init_url_type),
                urlType
            )

            val url = location.path
            putString(
                context.getString(R.string.pref_init_url),
                url
            )

            // Banner Standard
            config.layout.screens.banner?.standard?.let { placements ->
                putStringSet(
                    context.getString(R.string.pref_banner_placement_name),
                    placements.map { it.placementName }.toSet()
                )
            }

            // MREC
            config.layout.screens.banner?.mrec?.let { placements ->
                putStringSet(
                    context.getString(R.string.pref_mrec_placement_name),
                    placements.map { it.placementName }.toSet()
                )
            }

            // Native Small
            config.layout.screens.native?.small?.let { placements ->
                putStringSet(
                    context.getString(R.string.pref_native_small_placement_name),
                    placements.map { it.placementName }.toSet()
                )
            }

            // Native Medium
            config.layout.screens.native?.medium?.let { placements ->
                putStringSet(
                    context.getString(R.string.pref_native_medium_placement_name),
                    placements.map { it.placementName }.toSet()
                )
            }

            // Interstitial
            config.layout.screens.interstitial?.default?.let { placements ->
                putStringSet(
                    context.getString(R.string.pref_interstitial_placement_name),
                    placements.map { it.placementName }.toSet()
                )
            }

            // Rewarded
            config.layout.screens.rewarded?.default?.let { placements ->
                putStringSet(
                    context.getString(R.string.pref_rewarded_placement_name),
                    placements.map { it.placementName }.toSet()
                )
            }

            // Save the appKey too
            putString(context.getString(R.string.pref_app_key), config.appKey)

            // Save user email info for display
            config.SDKConfiguration.userInfo?.let { userInfo ->
                val delaySeconds = userInfo.userIdRegisteredAtMS?.div(1000) ?: 0
                val summary = when {
                    !userInfo.userEmail.isNullOrBlank() ->
                        "email=${userInfo.userEmail}, delay=${delaySeconds}s"

                    !userInfo.userEmailHashed.isNullOrBlank() ->
                        "hash=${userInfo.userEmailHashed.take(10)}..., delay=${delaySeconds}s"

                    else -> "(not set)"
                }

                putString(context.getString(R.string.pref_user_email), summary)
            } ?: run {
                putString(context.getString(R.string.pref_user_email), null)
            }
        }
    }
}
