package io.cloudx.adapter.cloudx

import android.app.Activity
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import io.cloudx.sdk.internal.adapter.Banner
import io.cloudx.sdk.internal.adapter.BannerContainer
import io.cloudx.sdk.internal.adapter.BannerListener
import io.cloudx.cd.staticrenderer.ExternalLinkHandlerImpl
import io.cloudx.cd.staticrenderer.StaticWebView

internal class StaticBidBanner(
    private val activity: Activity,
    private val container: BannerContainer,
    private val adm: String,
    private val listener: BannerListener
) : Banner {

    private val scope = CoroutineScope(Dispatchers.Main)

    private val staticWebView: StaticWebView by lazy {
        StaticWebView(activity, ExternalLinkHandlerImpl(activity))
    }

    override fun load() {
        staticWebView.clickthroughEvent
            .onEach { listener.onClick() }
            .launchIn(scope)

        scope.launch {
            staticWebView.hasUnrecoverableError.first { it }
            listener.onError()
        }

        scope.launch {
            container.onAdd(staticWebView)

            if (staticWebView.loadHtml(adm)) {
                listener.onLoad()

                staticWebView.visibility = View.VISIBLE

                listener.onShow()
                listener.onImpression()
            } else {
                listener.onError()
            }
        }
    }

    override fun destroy() {
        scope.cancel()
        staticWebView.destroy()
        container.onRemove(staticWebView)
    }
}