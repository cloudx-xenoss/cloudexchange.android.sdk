package io.cloudx.sdk.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * SDK related global coroutine scopes; DO NOT CANCEL THEM unless for testing purposes.
 */
internal object GlobalScopes {

    val IO = CoroutineScope(Dispatchers.IO)
}