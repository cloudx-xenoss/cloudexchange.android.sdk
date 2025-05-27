package io.cloudx.sdk.internal.common

import android.content.Context

internal fun Context.dpToPx(dp: Int): Int =
    dpToPx(dp, resources?.displayMetrics?.density ?: 1f)

internal fun Context.pxToDp(px: Int): Int =
    pxToDp(px, resources?.displayMetrics?.density ?: 1f)