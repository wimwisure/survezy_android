package com.github.wimwisure.survezy

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.material.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class Survezy(context: Context) {
    private val initialized = MutableLiveData(false)

    private val webView: WebView = getWebView(context)

    private val dialog by lazy {
        BottomSheetDialog(
            context,
            R.style.Theme_Design_BottomSheetDialog
        ).also {
            it.setContentView(webView)
            it.window?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                ?.setBackgroundResource(android.R.color.transparent)
            it.window?.setDimAmount(0.3f)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun getWebView(context: Context) = WebView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            (context.resources.displayMetrics.heightPixels * .7).toInt()
        )
        setBackgroundColor(Color.TRANSPARENT)
        settings.javaScriptEnabled = true
        loadUrl("file:///android_asset/survezy.html")

        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val i = Intent(Intent.ACTION_VIEW, request?.url)
                context.startActivity(i)
                return true;
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                initialized.value = true
            }
        }

        webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {

                when (consoleMessage?.message().orEmpty()) {
                    "close" -> dialog.hide()
                    "start" -> {
                        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                        dialog.show()
                    }
                }

                return super.onConsoleMessage(consoleMessage)
            }
        }
    }


    private val darkMode =
        context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    private val String.js get() = "survezy.Show('$this', ${if (darkMode) "true" else "false"})"

    fun show(eventId: String) {
        if (initialized.value == false) return observeShow(eventId)

        webView.evaluateJavascript(eventId.js, null)
    }

    private fun observeShow(eventId: String) {
        initialized.observeForever(object : Observer<Boolean> {
            override fun onChanged(value: Boolean) {
                if (value) {
                    webView.evaluateJavascript(eventId.js, null)
                    initialized.removeObserver(this)
                }
            }
        })
    }
}
