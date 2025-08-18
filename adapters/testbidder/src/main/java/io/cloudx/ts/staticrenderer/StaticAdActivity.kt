package io.cloudx.ts.staticrenderer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View.VISIBLE
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.appcompat.widget.AppCompatButton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first

// TODO. Consider single instance activity (not 1 activity per whole app) in AndroidManifest.
internal class StaticAdActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentActivity = this

        val webView = staticWebView
        if (webView == null) {
            Log.i("StaticAdActivity", "can't display ad: WebView is missing")
            dismiss()
            return

        }

        val fl = FrameLayout(this)

        webView.visibility = VISIBLE
        fl.addView(webView)

        val closeButton = AppCompatButton(this).apply {
            text = "Close"
            setOnClickListener { dismiss() }
        }

        fl.addView(
            closeButton,
            FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.TOP or Gravity.END)
        )

        setContentView(fl, FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))

        //AdWebViewScreen(webView, closeDelaySeconds = 1, Companion::dismiss)
    }

    override fun onDestroy() {
        super.onDestroy()
        currentActivity = null
    }

    companion object {

        private var staticWebView: StaticWebView? = null

        private var currentActivity: StaticAdActivity? = null

        private val dismiss = MutableStateFlow(false)

        private fun dismiss() {
            dismiss.value = true
        }

        // Doesn't support parallel calls, not required though.
        suspend fun show(activity: Activity, staticWebView: StaticWebView) {
            try {
                Companion.staticWebView = staticWebView
                activity.startActivity(Intent(activity, StaticAdActivity::class.java))

                dismiss.first { it }

            } finally {
                dismiss.value = false

                Companion.staticWebView = null
                currentActivity?.finish()
            }
        }
    }
}