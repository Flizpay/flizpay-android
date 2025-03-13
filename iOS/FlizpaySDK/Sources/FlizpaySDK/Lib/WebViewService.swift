import UIKit
import WebKit

public class FlizpayWebView: UIViewController {
    
    public var redirectUrl: URL?
    private var webView: WKWebView?
    private var webViewBridge: WebViewBridge? // ✅ Declare it with var
    
    public override func viewDidLoad() {
        super.viewDidLoad()
        setupWebView()
        
        if let url = redirectUrl {
            let request = URLRequest(url: url)
            webView?.load(request)
        }
    }

    public func present(from presentingVC: UIViewController, redirectUrl: String, jwt: String) {
        let flizpayWebView = FlizpayWebView()
        let redirectUrlWithJwtToken = "\(redirectUrl)&jwt=\(jwt)"
        print("url is", redirectUrlWithJwtToken)
        
        flizpayWebView.redirectUrl = URL(string: redirectUrlWithJwtToken)
        presentingVC.present(flizpayWebView, animated: true, completion: nil)
    }

    private func setupWebView() {
        let config = WKWebViewConfiguration()
        let contentController = WKUserContentController()
        config.userContentController = contentController
        config.preferences.javaScriptEnabled = true

        let wv = WKWebView(frame: .zero, configuration: config)
        wv.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(wv)

        NSLayoutConstraint.activate([
            wv.topAnchor.constraint(equalTo: view.topAnchor),
            wv.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            wv.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            wv.trailingAnchor.constraint(equalTo: view.trailingAnchor)
        ])

        self.webView = wv
        self.webViewBridge = WebViewBridge(webView: wv) 
        self.webViewBridge?.register(in: contentController)
        self.webViewBridge?.injectJavaScriptInterface()
    }
}
