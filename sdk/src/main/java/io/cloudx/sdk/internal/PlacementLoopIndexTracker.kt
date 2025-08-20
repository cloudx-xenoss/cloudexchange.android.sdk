package io.cloudx.sdk.internal

/**
 * Tracks loop index (i.e., how many times an ad has sent BidRequest)
 * for each placement within the SDK runtime.
 */
object PlacementLoopIndexTracker {
    private val loopIndexMap = mutableMapOf<String, Int>()

    fun getAndIncrement(placementName: String): Int {
        val current = loopIndexMap.getOrElse(placementName) { 0 }
        loopIndexMap[placementName] = current + 1
        println("hop: markAndGet() → placement=$placementName, returning=$current, next=${current+1}")
        return current
    }

    fun getCount(placementName: String): Int {
        val counter = loopIndexMap[placementName]
        return if (counter != null){
            counter - 1
        } else {
            loopIndexMap[placementName] = 0
            0
        }
    }

    fun reset(placementName: String) {
        println("hop: reset($placementName) → removed")
        loopIndexMap.remove(placementName)
    }

    fun resetAll() {
        loopIndexMap.clear()
    }
}

