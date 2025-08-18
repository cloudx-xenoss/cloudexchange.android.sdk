package io.cloudx.sdk.internal.lineitem.matcher

internal object MatcherRegistry {
    private val matchers = mutableMapOf<String, FilterMatcher>()

    fun register(key: String, matcher: FilterMatcher) {
        matchers[key] = matcher
    }

    fun get(key: String): FilterMatcher? = matchers[key]

    fun registerMatchers() {
        register(LoopIndexMatcher.key, LoopIndexMatcher)
    }
}
