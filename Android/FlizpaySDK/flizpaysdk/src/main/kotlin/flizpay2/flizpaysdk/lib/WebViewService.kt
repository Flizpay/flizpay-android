package flizpay2.flizpaysdk.lib

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class WebViewService : AppCompatActivity() {
    private var webView: WebView? = null
    private var redirectUrl: Uri? = null
    private lateinit var keychainService: KeychainService

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        setContentView(webView)

        webView?.settings?.javaScriptEnabled = true
        webView?.settings?.domStorageEnabled = true
        webView?.webChromeClient = WebChromeClient()
        webView?.webViewClient = WebViewClient()
    }

    /**
     * Presents the Flizpay web view modally using a redirect URL.
     * @param redirectUrl The URL to which the web view should navigate.
     * @param token JWT token for authentication.
     * @param email User's email for the transaction.
     */
    fun present(
        redirectUrl: String,
        token: String,
        email: String,
        keychainAccessKey: String
    ) {
        val redirectUrlWithJwtToken = "$redirectUrl&jwttoken=$token&email=$email"
        println("URL is $redirectUrlWithJwtToken")
        
        this.redirectUrl = Uri.parse(redirectUrlWithJwtToken)
        keychainService = KeychainService(this, keychainAccessKey)
        val webViewBridge = WebViewBridge(keychainService, webView as WebView)

        webView?.addJavascriptInterface(webViewBridge, "KeychainBridge")
        webView?.loadUrl(redirectUrlWithJwtToken)
    }
}
