import WebKit
import Security

class WebViewBridge: NSObject, WKScriptMessageHandler {
    
    private weak var webView: WKWebView?
    
    init(webView: WKWebView) {
        self.webView = webView
    }
    
    // Injects the JavaScript bridge into the WebView
    func injectJavaScriptInterface() {
        let js = """
        window.KeychainBridge = {
            saveCredentials: function(key, value) {
                window.webkit.messageHandlers.saveCredentials.postMessage({ key: key, value: value });
            },
            getCredentials: function(key) {
                window.webkit.messageHandlers.getCredentials.postMessage({ key: key });
            },
            clearCredentials: function(key) {
                window.webkit.messageHandlers.clearCredentials.postMessage({ key: key });
            }
        };
        """
        
        let script = WKUserScript(source: js, injectionTime: .atDocumentStart, forMainFrameOnly: false)
        webView?.configuration.userContentController.addUserScript(script)
    }
    
    // Registers message handlers
    func register(in userContentController: WKUserContentController) {
        userContentController.add(self, name: "saveCredentials")
        userContentController.add(self, name: "getCredentials")
        userContentController.add(self, name: "clearCredentials")
    }
    
    // Handles incoming messages from JavaScript
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        guard let body = message.body as? [String: Any] else { return }
        
        switch message.name {
        case "saveCredentials":
            if let key = body["key"] as? String, let value = body["value"] as? String {
                KeychainService.storeCredentials(key: key, value: value)
            }
        case "getCredentials":
            if let key = body["key"] as? String {
                if let credentials = KeychainService.fetchCredentials(key: key) {
                    let js = "window.onReceiveCredentials('\(credentials)');"
                    DispatchQueue.main.async {
                        self.webView?.evaluateJavaScript(js, completionHandler: nil)
                    }
                }
            }
        case "clearCredentials":
            if let key = body["key"] as? String {
                KeychainService.clearCredentials(key: key)
            }
        default:
            break
        }
    }
}
