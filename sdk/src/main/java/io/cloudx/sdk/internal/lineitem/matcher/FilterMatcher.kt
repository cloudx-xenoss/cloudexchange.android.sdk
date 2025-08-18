package io.cloudx.sdk.internal.lineitem.matcher

import io.cloudx.sdk.internal.lineitem.MatchContext

internal interface FilterMatcher {
    val key: String
    fun matches(context: MatchContext, key: String, value: Any): Boolean
}
