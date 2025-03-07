import UIKit
import WebKit

public class FlizpayWebView: UIViewController, WKScriptMessageHandler {
    // Public properties so they can be set before presenting
    public var redirectUrl: URL? = URL(string: "");
    private var webView: WKWebView?
    
    // MARK: - Life Cycle
    public override func viewDidLoad() {
        super.viewDidLoad();
        setupWebView();
    }
    
    // MARK: - Public API
    
    /// Presents the Flizpay web view modally using a redirect URL.
    /// - Parameters:
    ///   - presentingVC: The UIViewController from which to present the web view.
    ///   - redirectUrl: The URL to which the web view should navigate.
    public func present(
        from presentingVC: UIViewController,
        redirectUrl: String,
        token: String,
        email: String
    ) {
        let flizpayWebView = FlizpayWebView();
        // Just append & token & email directly
        let redirectUrlWithJwtToken = "\(redirectUrl)&jwttoken=\(token)&email=\(email)"
        print("url is", redirectUrlWithJwtToken)
        // Add the url to the webview
        flizpayWebView.redirectUrl = URL(string: redirectUrlWithJwtToken);
        // Load the url in the webview
        webView?.load(URLRequest(url: flizpayWebView.redirectUrl!));
        // Present the webview in the current controller
        presentingVC.present(flizpayWebView, animated: true, completion: nil)
    }
    
    // MARK: - Setup Methods
    private func setupWebView() {
        let config = WKWebViewConfiguration()
        // If needed for JS messages, uncomment the next line:
        // config.userContentController.add(self, name: "sdkHandler")
        
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
    }
    
    // MARK: - WKScriptMessageHandler
    public func userContentController(_ userContentController: WKUserContentController,
                                      didReceive message: WKScriptMessage) {
        // Handle JavaScript messages if needed.
    }
}
