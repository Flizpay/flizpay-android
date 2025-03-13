package flizpay2.flizpaysdk.lib

import android.webkit.JavascriptInterface
import android.webkit.WebView

/**
 * Usage inside the JS webview
 * // Save credentials
 * window.KeychainBridge.saveCredentials("bank_user", "secure_value");
 *
 * // Retrieve credentials
 * window.KeychainBridge.getCredentials("bank_user");
 *
 * // Clear Credentials
 * window.KeyChainBridge.clearCredentials("bank_user");
 *
 * // Inject credentials
 * window.onReceiveCredentials = function(value) {
 *     console.log("Received credentials: ", value);
 * };
 */

class WebViewBridge(private val keychainService: KeychainService, private val webView: WebView) {

    @JavascriptInterface
    fun saveCredentials(key: String, value: String) {
        keychainService.storeCredentials(key, value)
    }

    @JavascriptInterface
    fun getCredentials(key: String): String {
        return keychainService.fetchCredentials(key) ?: ""
    }

    @JavascriptInterface
    fun clearCredentials(key: String) {
        return keychainService.clearCredentials(key)
    }

    fun injectCredentials(key: String) {
        val credentials = keychainService.fetchCredentials(key)
        credentials?.let {
            webView.post {
                webView.evaluateJavascript("window.onReceiveCredentials('$it');", null)
            }
        }
    }
}
