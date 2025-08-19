package io.cloudx.sdk.internal.lineitem.matcher

import io.cloudx.sdk.internal.lineitem.MatchContext

object LoopIndexMatcher : FilterMatcher {
    override val key = "loop-index"
    override fun matches(context: MatchContext, key: String, value: Any): Boolean {
        return context.loopIndex == (value as? Int)
    }
}
