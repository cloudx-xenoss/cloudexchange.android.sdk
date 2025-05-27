package io.cloudx.sdk.internal.state

internal object SdkUserState {
    @Volatile
    var hashedUserId: String? = null
}
