package io.cloudx.sdk.internal.lineitem

import io.cloudx.sdk.internal.config.Config
import io.cloudx.sdk.internal.lineitem.matcher.DefaultKeyValueMatcher
import io.cloudx.sdk.internal.lineitem.matcher.MatcherRegistry

internal object LineItemEvaluator {

    fun evaluateTargeting(
        targeting: Config.LineItem.Targeting,
        context: MatchContext
    ): Boolean {
        println("🧠 Evaluating targeting block (conditionsAnd=${targeting.conditionsAnd})")

        val results = targeting.conditions.mapIndexed { index, condition ->
            val result = evaluateCondition(condition, context)
            println("  🔸 Condition[$index] result = $result")
            result
        }

        val final = if (targeting.conditionsAnd) results.all { it } else results.any { it }
        println("✅ Final targeting match result = $final")
        return final
    }

    private fun evaluateCondition(
        condition: Config.LineItem.Condition,
        context: MatchContext
    ): Boolean {
        println("  🧩 Evaluating condition: whitelist=${condition.whitelist}, blacklist=${condition.blacklist}, and=${condition.and}")

        return when {
            condition.whitelist.isNotEmpty() ->
                evaluateFilterList(condition.whitelist, context, condition.and, isWhitelist = true)

            condition.blacklist.isNotEmpty() ->
                !evaluateFilterList(condition.blacklist, context, condition.and, isWhitelist = false)

            else -> {
                println("  ⚠️ Empty condition detected → defaulting to TRUE")
                true
            }
        }
    }

    private fun evaluateFilterList(
        filters: List<Map<String, Any>>,
        context: MatchContext,
        useAnd: Boolean,
        isWhitelist: Boolean
    ): Boolean {
        println("    ➕ Evaluating ${if (isWhitelist) "whitelist" else "blacklist"} filters (AND=$useAnd)")

        val results = filters.mapIndexed { index, filter ->
            val entryResults = filter.entries.map { (key, value) ->
                val matcher = MatcherRegistry.get(key) ?: DefaultKeyValueMatcher
                val matched = matcher.matches(context, key, value)

                println("      🔍 Filter[$index] → key=\"$key\", value=\"$value\" ➜ match=$matched")
                matched
            }

            val filterResult = if (useAnd) entryResults.all { it } else entryResults.any { it }
            println("      🔄 Filter[$index] result = $filterResult (based on AND=$useAnd)")
            filterResult
        }

        val finalResult = if (useAnd) results.all { it } else results.any { it }
        println("    🧾 Overall ${if (isWhitelist) "whitelist" else "blacklist"} result = $finalResult")
        return finalResult
    }
}

