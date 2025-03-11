import UIKit
import WebKit

public class FlizpayWebView: UIViewController, WKScriptMessageHandler {
    
    public var redirectUrl: URL?
    private var webView: WKWebView?
    
    // MARK: - Life Cycle
    public override func viewDidLoad() {
        super.viewDidLoad()
        setupWebView()
        
        // Now that the webView is created, load the URL (if any)
        if let url = redirectUrl {
            let request = URLRequest(url: url)
            webView?.load(request)
        }
    }
    
    // MARK: - Public API
    public func present(
        from presentingVC: UIViewController,
        redirectUrl: String,
        jwt: String
    ) {
        let flizpayWebView = FlizpayWebView()
        
        // Build the full URL
        let redirectUrlWithJwtToken = "\(redirectUrl)&jwt=\(jwt)"
        print("url is", redirectUrlWithJwtToken)
        
        // Store the URL in the new instance
        flizpayWebView.redirectUrl = URL(string: redirectUrlWithJwtToken)
        
        // Present the new instance
        presentingVC.present(flizpayWebView, animated: true, completion: nil)
    }
    
    // MARK: - Setup Methods
    private func setupWebView() {
        let config = WKWebViewConfiguration()
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
        // Handle JS messages if needed
    }
}
