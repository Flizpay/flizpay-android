package flizpay2.flizpaysdk.lib

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import flizpay2.flizpaysdk.Constants


class WebViewService : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get Intent Data
        val redirectUrl = intent.getStringExtra("redirectUrl") ?: return
        // This callback scheme brings the user back to the host app's WebView after
        // the bank app finishes an external authorization flow (for example Revolut).
        val urlScheme = intent.getStringExtra("urlScheme") ?: return
        val token = intent.getStringExtra("token") ?: return

        // Instantiate WebView
        val webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        // Viewport settings for proper content scaling and positioning
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true

        // Register WebViewBridge to intercept window.close()
        val webViewBridge = WebViewBridge(webView, this)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url ?: return false
                val host = url.host ?: return false

                // If the URL is HTTPS and host is in the noCredentialsBankHosts list,
                // open it externally instead of loading in WebView
                if (url.scheme.equals("https", ignoreCase = true) &&
                    Constants.NO_CREDS_BANKS.any { host.contains(it) }
                ) {
                    val intent = Intent(Intent.ACTION_VIEW, url)
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    }
                    return true // Cancel loading inside the WebView
                }
                return false // Allow the WebView to load the URL
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // Re-apply the bridge after each navigation so payer-web can close the
                // activity even after redirects or full page loads.
                webViewBridge.overrideWindowClose()
            }
        }


        runOnUiThread {
            webView.webChromeClient = WebChromeClient()

            // Set content
            setContentView(webView)

            // Pass the host app callback scheme to payer-web so it can register the
            // return URL for redirect-based bank authorization flows.
            val redirectUrlWithJwtToken = "$redirectUrl&jwt=$token&redirect-url=$urlScheme"

            // Load the URL
            webView.loadUrl(redirectUrlWithJwtToken)
        }
    }
}
