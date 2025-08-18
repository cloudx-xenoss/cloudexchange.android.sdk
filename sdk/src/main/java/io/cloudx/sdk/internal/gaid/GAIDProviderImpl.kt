package io.cloudx.sdk.internal.gaid

import android.content.Context
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class GAIDProviderImpl(
    private val context: Context
) : GAIDProvider {

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun invoke() = withContext(Dispatchers.IO) {
        runCatching {
            AdvertisingIdClient.getAdvertisingIdInfo(context)
        }.getOrNull()?.run {
            GAIDProvider.Result(
                gaid = if (isLimitAdTrackingEnabled) ANON_AD_ID else id ?: ANON_AD_ID,
                isLimitAdTrackingEnabled = isLimitAdTrackingEnabled
            )
        } ?: GAIDProvider.Result(
            gaid = ANON_AD_ID,
            isLimitAdTrackingEnabled = true
        )
    }
}

private const val ANON_AD_ID = "00000000-0000-0000-0000-000000000000"