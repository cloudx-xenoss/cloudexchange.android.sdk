package io.cloudx.sdk

interface CloudXIsAdLoadedListener {

    /**
     * On is ad loaded status changed
     *
     * @param isAdLoaded true - ad is loaded and ready to be displayed. false - no ads or is loading now.
     */
    fun onIsAdLoadedStatusChanged(isAdLoaded: Boolean)
}