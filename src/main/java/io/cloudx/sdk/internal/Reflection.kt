package io.cloudx.sdk.internal

/**
 * __Note: doesn't work with ```private object...``` modifiers__
 * @param T - type to which [fullClassName] should be resolved to
 * @return kotlin object instance if className exists; null otherwise
 */
internal inline fun <reified T> objectInstance(fullClassName: String): T? = try {
    Class.forName(fullClassName).kotlin.objectInstance as? T
} catch (e: Exception) {
    Logger.e("classForName", "couldn't resolve fullClassName: $fullClassName", e)
    null
}