package io.cloudx.demo.demoapp

import io.cloudx.sdk.BasePublisherListener
import io.cloudx.sdk.CloudXAd
import io.cloudx.sdk.CloudXAdError
import io.cloudx.sdk.internal.CloudXLogger

class LoggedBasePublisherListener(
    private val logTag: String,
    private val placementName: String
) : BasePublisherListener {

    override fun onAdLoadSuccess(cloudXAd: CloudXAd) {
        CloudXLogger.info(
            logTag,
            "Load Success; placement: $placementName; network: ${cloudXAd.networkName}"
        )
    }

    override fun onAdLoadFailed(cloudXAdError: CloudXAdError) {
        CloudXLogger.info(logTag, "LOAD FAILED; placement: $placementName;")
    }

    override fun onAdShowSuccess(cloudXAd: CloudXAd) {
//        CloudXLogger.info(
//            logTag,
//            "Ad shown — placement: $placementName, network: ${cloudXAd.networkName}"
//        )
    }

    override fun onAdShowFailed(cloudXAdError: CloudXAdError) {
        CloudXLogger.info(logTag, "SHOW FAILED; placement: $placementName;")
    }

    override fun onAdHidden(cloudXAd: CloudXAd) {
//        CloudXLogger.info(
//            logTag,
//            "Ad hidden; placement: $placementName; network: ${cloudXAd.networkName}"
//        )
    }

    override fun onAdClicked(cloudXAd: CloudXAd) {
        CloudXLogger.info(
            logTag,
            "Ad clicked; placement: $placementName; network: ${cloudXAd.networkName}"
        )
    }
}