package io.cloudx.sdk.internal.screen

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

internal class ScreenServiceImpl(
    private val activity: Activity
) : ScreenService {

    override suspend fun invoke(): ScreenService.ScreenData {
        val windowManager = ContextCompat.getSystemService(activity, WindowManager::class.java)!!

        return with(windowManager) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                fromApi30AndAbove(activity)
            } else {
                legacy()
            }
        }
    }

    @RequiresApi(30)
    private fun WindowManager.fromApi30AndAbove(context: Context): ScreenService.ScreenData {
        val metrics = maximumWindowMetrics
        val bounds = metrics.bounds

        val resources = context.resources

        val densityDpi = resources.displayMetrics.densityDpi
        val density = resources.displayMetrics.density

        val wPx = bounds.width()
        val hPx = bounds.height()

        return ScreenService.ScreenData(
            wPx,
            hPx,
            pxToDp(wPx, density),
            pxToDp(hPx, density),
            densityDpi,
            density
        )
    }

    private fun WindowManager.legacy(): ScreenService.ScreenData {
        val dm = DisplayMetrics()
        defaultDisplay?.getRealMetrics(dm)

        return ScreenService.ScreenData(
            dm.widthPixels,
            dm.heightPixels,
            pxToDp(dm.widthPixels, dm.density),
            pxToDp(dm.heightPixels, dm.density),
            dm.densityDpi,
            dm.density
        )
    }
}