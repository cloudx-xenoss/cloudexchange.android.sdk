package io.cloudx.sdk.internal.screen

import android.app.Activity

internal interface ScreenService {

    suspend operator fun invoke(): ScreenData

    class ScreenData(
        val widthPx: Int,
        val heightPx: Int,
        val widthDp: Int,
        val heightDp: Int,
        val dpi: Int,
        val pxRatio: Float
    )
}

internal fun ScreenService(activity: Activity): ScreenService =
    ScreenServiceImpl(activity)