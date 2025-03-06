import UIKit

public class PaymentController {
    
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
        onFailure: ((Error) -> Void)? = nil
    ) {
        // 1. Call the transaction service with the token & amount.
        let transactionService = TransactionService()
        transactionService.fetchTransactionInfo(token: token, amount: amount) { result in
            DispatchQueue.main.async {
                switch result {
                case .success(let transactionResponse):
                    // 2. Present the Flizpay web view using the redirect URL.
                    FlizpayWebView.present(
                        from: presentingVC,
                        redirectUrl: transactionResponse.redirectUrl ?? "https://secure.flizpay.de"
                    )
                    
                case .failure(let error):
                    // If the transaction call fails, let the host app handle it.
                    onFailure?(error)
                }
            }
        }
    }
}
