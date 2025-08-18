package io.cloudx.ts.staticrenderer

import android.app.Activity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import io.cloudx.sdk.internal.FullscreenAd

fun StaticFullscreenAd(
    activity: Activity,
    adm: String,
    listener: FullscreenAd.Listener?
): FullscreenAd<FullscreenAd.Listener> =
    StaticFullscreenAdImpl(
        activity,
        adm,
        listener
    )

private class StaticFullscreenAdImpl(
    private val activity: Activity,
    private val adm: String,
    override var listener: FullscreenAd.Listener?
) : FullscreenAd<FullscreenAd.Listener> {

    private val scope = CoroutineScope(Dispatchers.Main)

    private val staticWebView: StaticWebView by lazy {
        StaticWebView(activity, ExternalLinkHandlerImpl(activity))
    }

    override fun load() {
        staticWebView.clickthroughEvent
            .onEach { listener?.onClick() }
            .launchIn(scope)

        scope.launch {
            staticWebView.hasUnrecoverableError.first { it }
            onError()
        }

        scope.launch {
            if (staticWebView.loadHtml(adm)) listener?.onLoad() else listener?.onLoadError()
        }
    }

    override fun show() {
        scope.launch {
            showCalled = true

            listener?.run {
                onShow()
                onImpression()
            }

            try {
                StaticAdActivity.show(activity, staticWebView)
            } finally {
                listener?.onComplete()
                listener?.onHide()
            }
        }
    }

    private var showCalled = false

    private fun onError() {
        if (showCalled) listener?.onShowError() else listener?.onLoadError()
    }

    override fun destroy() {
        scope.cancel()
        staticWebView.destroy()
    }
}