import UIKit
import WebKit

public class FlizpayWebView: UIViewController, WKScriptMessageHandler {
    // Public properties so they can be set before presenting
    public var email: String = ""
    public var amount: String = ""
    public var redirectUrl: String? = nil
    
    private var webView: WKWebView?
    
    // MARK: - Life Cycle
    public override func viewDidLoad() {
        super.viewDidLoad()
        setupWebView()
        loadFlizpayURL()
    }
    
    // MARK: - Public API
    
    /// Presents the Flizpay web view modally using email and amount.
    /// - Parameters:
    ///   - presentingVC: The UIViewController from which to present the web view.
    ///   - email: The email to pass as a URL query parameter.
    ///   - amount: The amount to pass as a URL query parameter.
    public static func present(from presentingVC: UIViewController, email: String, amount: String) {
        let flizpayWebView = FlizpayWebView()
        flizpayWebView.email = email
        flizpayWebView.amount = amount
        presentingVC.present(flizpayWebView, animated: true, completion: nil)
    }
    
    /// Presents the Flizpay web view modally using a redirect URL.
    /// - Parameters:
    ///   - presentingVC: The UIViewController from which to present the web view.
    ///   - redirectUrl: The URL to which the web view should navigate.
    public static func present(from presentingVC: UIViewController, redirectUrl: String) {
        let flizpayWebView = FlizpayWebView()
        flizpayWebView.redirectUrl = redirectUrl
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
    
    private func loadFlizpayURL() {
        let urlString: String
        // If a redirect URL is provided, use it; otherwise, construct the URL using email & amount.
        if let redirectUrl = redirectUrl, !redirectUrl.isEmpty {
            urlString = redirectUrl
        } else {
            let baseURLString = "https://secure-staging.flizpay.de"
            urlString = "\(baseURLString)?email=\(email)&amount=\(amount)"
        }
        
        if let url = URL(string: urlString) {
            let request = URLRequest(url: url)
            webView?.load(request)
        } else {
            print("Invalid URL string: \(urlString)")
        }
    }
    
    // MARK: - WKScriptMessageHandler
    public func userContentController(_ userContentController: WKUserContentController,
                                      didReceive message: WKScriptMessage) {
        // Handle JavaScript messages if needed.
    }
}
