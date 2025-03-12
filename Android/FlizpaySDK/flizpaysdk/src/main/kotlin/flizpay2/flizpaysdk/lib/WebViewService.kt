package flizpay2.flizpaysdk.lib

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class WebViewService : AppCompatActivity() {
    private lateinit var keychainService: KeychainService

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get Intent Data
        val redirectUrl = intent.getStringExtra("redirectUrl") ?: return
        val token = intent.getStringExtra("token") ?: return
        val keychainAccessKey = intent.getStringExtra("keychainAccessKey") ?: "flizpay_keychain_access_key"

        // Instantiate Keychain Service
        keychainService = KeychainService(this, keychainAccessKey)

        // Instantiate WebView
        val webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        runOnUiThread {
            // Add keychain bridge
            webView.addJavascriptInterface(
                WebViewBridge(keychainService, webView),
                "KeychainBridge"
            )
            webView.webChromeClient = WebChromeClient()
            webView.webViewClient = WebViewClient()

            // Set content
            setContentView(webView)

            // Load redirect url
            val redirectUrlWithJwtToken = "$redirectUrl&jwt=$token"
            println("URL is $redirectUrlWithJwtToken")
            webView.loadUrl(redirectUrlWithJwtToken)
        }
    }
}
