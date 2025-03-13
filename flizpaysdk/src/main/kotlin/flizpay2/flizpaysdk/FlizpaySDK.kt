package flizpay2.flizpaysdk


import android.content.Context
import android.content.Intent
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
     * @param onFailure Optional callback to handle errors (e.g., show alerts).
     * @param keychainAccessKey Optional access key for storing and retrieving bank credentials from the device keychain
     */
    fun initiatePayment(
        context: Context,
        token: String,
        amount: String,
        onFailure: ((Throwable) -> Unit)? = null,
        keychainAccessKey: String?
    ) {
        val transactionService = TransactionService()

        transactionService.fetchTransactionInfo(token, amount) { result ->
            if(result.isSuccess) {
                val redirectUrl = result.getOrNull() ?: Constants.BASE_URL

                println("Starting FLIZ webview")

                val intent = Intent(context, WebViewService::class.java).apply {
                    putExtra("redirectUrl", redirectUrl)
                    putExtra("token", token)
                    putExtra("keychainAccessKey", keychainAccessKey)
                }

                // Ensure it's an activity context
                if (context !is AppCompatActivity) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)  // Needed for non-Activity context
                }

                context.startActivity(intent)
            } else {
                val error = result.exceptionOrNull()?.message ?: "Unknown error"
                println("TransactionService failed: $error")
                onFailure?.invoke(result.exceptionOrNull() ?: Throwable("Unknown Result"))
            }
        }
    }
}
