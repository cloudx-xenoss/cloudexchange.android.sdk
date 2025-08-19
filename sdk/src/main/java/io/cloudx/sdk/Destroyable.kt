package io.cloudx.sdk

interface Destroyable {

    /**
     * Release any unmanaged resources. Mostly used for ad instances, when Activity is destroyed, or [Destroyable] instance is not required anymore.
     */
    fun destroy()
}