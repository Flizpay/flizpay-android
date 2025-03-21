package flizpay2.flizpaysdk.lib

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class WebViewService : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get Intent Data
        val redirectUrl = intent.getStringExtra("redirectUrl") ?: return
        val token = intent.getStringExtra("token") ?: return

        // Instantiate WebView
        val webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        // Register WebViewBridge to intercept window.close()
        val webViewBridge = WebViewBridge(webView) {
            runOnUiThread { finish() } // Close activity when triggered
        }

        runOnUiThread {
            webView.webChromeClient = WebChromeClient()
            webView.webViewClient = WebViewClient()

            // Set content
            setContentView(webView)

            // Load redirect URL with token
            val redirectUrlWithJwtToken = "$redirectUrl&jwt=$token"
            webView.loadUrl(redirectUrlWithJwtToken)

            // Override window.close behavior
            webViewBridge.overrideWindowClose()
        }
    }
}
