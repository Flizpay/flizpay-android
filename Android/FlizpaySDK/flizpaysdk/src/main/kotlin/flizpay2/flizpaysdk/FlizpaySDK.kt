package flizpay2.flizpaysdk

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import flizpay2.flizpaysdk.lib.TransactionService
import flizpay2.flizpaysdk.lib.WebViewService

object FlizpaySDK {

    /**
     * Initiates the payment flow within your SDK.
     *
     * @param context The Context from which to launch the payment WebView.
     * @param token The JWT token fetched by the host app.
     * @param amount The transaction amount.
     * @param email The user's email for the transaction.
     * @param onFailure Optional callback to handle errors (e.g., show alerts).
     */
    fun initiatePayment(
            context: Context,
            token: String,
            amount: String,
            email: String,
            onFailure: ((Throwable) -> Unit)? = null
    ) {
        val transactionService = TransactionService()

        transactionService.fetchTransactionInfo(token, amount) { result ->
            when (result) {
                is Result.Success -> {
                    val redirectUrl = result.data.redirectUrl ?: Constants.BASE_URL

                    if (context is AppCompatActivity) {
                        WebViewService()
                                .present(
                                        context = context,
                                        redirectUrl = redirectUrl,
                                        token = token,
                                        email = email
                                )
                    }
                }
                is Result.Failure -> {
                    onFailure?.invoke(result.exception)
                }
            } else {
                onFailure?.invoke(IllegalStateException("Invalid result type"))
            }
        }
    }
}
