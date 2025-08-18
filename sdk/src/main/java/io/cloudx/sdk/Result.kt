package io.cloudx.sdk

/**
 * @suppress
 * Helper class, purposed mainly as an explicit success/failure function result.
 */
sealed class Result<R, E> {

    class Success<R, E>(val value: R) : Result<R, E>()
    class Failure<R, E>(val value: E) : Result<R, E>()
}