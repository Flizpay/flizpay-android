package flizpay2.flizpaysdk


import android.content.Context
import android.content.Intent
import flizpay2.flizpaysdk.lib.TransactionService
import flizpay2.flizpaysdk.lib.WebViewService

object FlizpaySDK {
    /**
     * Initiates the payment flow within your SDK.
     *
     * @param context The Context from which to launch the payment WebView.
     * @param token The JWT token fetched by the host app.
     * @param amount The transaction amount.
     * @param metadata The metadata object.
     * @param urlScheme The host app callback URL used for redirect-based bank flows.
     * @param onFailure Optional callback to handle errors (e.g., show alerts).
     */
    fun initiatePayment(
        context: Context,
        token: String,
        amount: String,
        metadata: Map<String, Any?>? = null,
        urlScheme: String,
        onFailure: ((Throwable) -> Unit)? = null,
    ) {
        val transactionService = TransactionService()

        transactionService.fetchTransactionInfo(token, amount, metadata) { result ->
            if(result.isSuccess) {
                val redirectUrl = result.getOrNull() ?: Constants.BASE_URL

                val intent = Intent(context, WebViewService::class.java).apply {
                    putExtra("redirectUrl", redirectUrl)
                    putExtra("token", token)
                    putExtra("urlScheme", urlScheme)
                }

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                context.startActivity(intent)
            } else {
                val error = result.exceptionOrNull()?.message ?: "Unknown error"
                onFailure?.invoke(result.exceptionOrNull() ?: Throwable(error))
            }
        }
    }
}
