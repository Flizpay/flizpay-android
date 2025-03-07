import UIKit

public class FlizpaySDK {
    
    /// Initiates the payment flow within your SDK.
    ///
    /// - Parameters:
    ///   - presentingVC: The UIViewController from which to present the payment web view.
    ///   - token: The JWT token fetched by the host app.
    ///   - amount: The transaction amount.
    ///   - onFailure: Optional closure if you want to handle errors (e.g., show alerts).
    public static func initiatePayment(
        from presentingVC: UIViewController,
        token: String,
        amount: String,
        email: String,
        onFailure: ((Error) -> Void)? = nil
    ) {
        let transactionService = TransactionService()
        transactionService.fetchTransactionInfo(token: token, amount: amount) { result in
            DispatchQueue.main.async {
                switch result {
                case .success(let transactionResponse):
                    // Unwrap the redirectUrl or use default
                    let redirectUrl = transactionResponse.redirectUrl ?? Constants.baseURL
                    
                    // Present the web view
                    FlizpayWebView().present(
                        from: presentingVC,
                        redirectUrl: redirectUrl,
                        token: token,
                        email: email
                    )
                    
                case .failure(let error):
                    onFailure?(error)
                }
            }
        }
    }
}
