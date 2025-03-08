package flizpay2.flizpaysdk.lib

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class WebViewService : AppCompatActivity() {
    private var webView: WebView? = null
    private var redirectUrl: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        setContentView(webView)
        setupWebView()
    }

    /**
     * Presents the Flizpay web view modally using a redirect URL.
     * @param context The context from which to launch the web view.
     * @param redirectUrl The URL to which the web view should navigate.
     * @param token JWT token for authentication.
     * @param email User's email for the transaction.
     */
    fun present(
        context: Context,
        redirectUrl: String,
        token: String,
        email: String
    ) {
        val redirectUrlWithJwtToken = "$redirectUrl&jwttoken=$token&email=$email"
        println("URL is $redirectUrlWithJwtToken")
        
        this.redirectUrl = Uri.parse(redirectUrlWithJwtToken)
        webView?.loadUrl(redirectUrlWithJwtToken)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView?.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?, request: WebResourceRequest?
                ): Boolean {
                    return false
                }
            }
        }
    }
}
