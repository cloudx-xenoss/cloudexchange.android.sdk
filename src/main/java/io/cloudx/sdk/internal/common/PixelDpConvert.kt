package io.cloudx.sdk.internal.common

// TODO. Move?
internal fun dpToPx(dp: Int, density: Float): Int = (dp * density).toInt()

internal fun pxToDp(px: Int, density: Float): Int = (px / density).toInt()
