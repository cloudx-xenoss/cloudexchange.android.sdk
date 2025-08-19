package io.cloudx.demo.demoapp.loglistview

import io.cloudx.demo.demoapp.MainActivity

// Common Filtered and renamed log tag rules for any screen (banner, int, rew)
fun commonLogTagListRules(forTag: String): String? = when (forTag) {
    MainActivity.TAG -> SDK
    "BidAdSourceImpl" -> "Bidding"
    "Endpoints" -> "SDK"
    else -> if (forTag.endsWith("Initializer")) forTag else null
}

private const val SDK = "SDK"