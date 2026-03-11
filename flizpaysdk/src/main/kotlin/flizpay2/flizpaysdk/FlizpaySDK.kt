package flizpay2.flizpaysdk


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import flizpay2.flizpaysdk.lib.TransactionService
import flizpay2.flizpaysdk.lib.WebViewService

/**
 * Configuration for FlizPay SDK URL overrides.
 * 
 * @param apiUrl Optional override for the API URL (defaults to production if null or empty)
 * @param baseUrl Optional override for the base URL (defaults to production if null or empty)
 */
data class FlizpayConfig(
    val apiUrl: String? = null,
    val baseUrl: String? = null
)

object FlizpaySDK {

    /**
     * Initiates the payment flow within your SDK.
     *
     * @param context The Context from which to launch the payment WebView.
     * @param token The JWT token fetched by the host app.
     * @param amount The transaction amount.
     * @param metadata The metadata object.
     * @param config Optional configuration for URL overrides.
     * @param urlScheme The host app callback URL used for redirect-based bank flows.
     * @param onFailure Optional callback to handle errors (e.g., show alerts).
     */
    fun initiatePayment(
        context: Context,
        token: String,
        amount: String,
        metadata: Map<String, Any?>? = null,
        config: FlizpayConfig? = null,
        urlScheme: String,
        onFailure: ((Throwable) -> Unit)? = null,
    ) {
        val transactionService = TransactionService(
            apiUrl = config?.apiUrl,
            baseUrl = config?.baseUrl
        )
        
        // Use configured base URL or fall back to production default
        val effectiveBaseUrl = config?.baseUrl?.takeIf { it.isNotEmpty() } ?: Constants.BASE_URL

        transactionService.fetchTransactionInfo(token, amount, metadata) { result ->
            if(result.isSuccess) {
                val redirectUrl = result.getOrNull() ?: effectiveBaseUrl

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
