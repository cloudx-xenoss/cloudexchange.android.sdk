package io.cloudx.sdk.internal.lineitem.matcher

import io.cloudx.sdk.internal.lineitem.MatchContext

internal object DefaultKeyValueMatcher : FilterMatcher {
    override val key: String = "*"

    override fun matches(context: MatchContext, key: String, value: Any): Boolean {
        val publisherValue = context.keyValue[key]
        return publisherValue != null && publisherValue.equals(value.toString(), ignoreCase = false)
    }
}
