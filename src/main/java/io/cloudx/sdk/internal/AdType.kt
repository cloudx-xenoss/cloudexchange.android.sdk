package io.cloudx.sdk.internal

import io.cloudx.sdk.internal.nativead.NativeAdSpecs
import io.cloudx.sdk.internal.nativead.NativeMediumImage
import io.cloudx.sdk.internal.nativead.NativeSmall

sealed class AdType {
    /**
     * Fullscreen non-rewarded ad.
     */
    data object Interstitial : AdType()

    /**
     * Fullscreen rewarded ad.
     */
    data object Rewarded : AdType()

    sealed class Banner(val size: AdViewSize) : AdType() {
        /**
         * 320x50 banner
         */
        data object Standard : Banner(AdViewSize.Standard)

        /**
         * 300x250 MREC
         */
        data object MREC : Banner(AdViewSize.MREC)
    }

    sealed class Native(val specs: NativeAdSpecs, val size: AdViewSize) : AdType() {

        data object Small : Native(NativeSmall, AdViewSize(320, 90))
        data object Medium : Native(NativeMediumImage, AdViewSize(320, 250))
    }
}