package io.cloudx.sdk.internal.screen

import android.content.Context

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

internal fun ScreenService(context: Context): ScreenService =
    ScreenServiceImpl(context)