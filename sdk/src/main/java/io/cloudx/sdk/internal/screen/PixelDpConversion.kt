package io.cloudx.sdk.internal.screen

internal fun dpToPx(dp: Int, density: Float): Int = (dp * density).toInt()

internal fun pxToDp(px: Int, density: Float): Int = (px / density).toInt()
