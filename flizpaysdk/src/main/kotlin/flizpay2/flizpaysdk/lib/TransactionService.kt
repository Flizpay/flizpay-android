package flizpay2.flizpaysdk.lib

import okhttp3.*
import java.io.IOException

import flizpay2.flizpaysdk.Constants
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class TransactionService {
    private val client = OkHttpClient()

    /**
     * Calls the /transactions endpoint using the provided token and amount.
     */
    fun fetchTransactionInfo(
        token: String,
        amount: String,
        completion: (Result<String>) -> Unit
    ) {
        val url = "${Constants.API_URL}/transactions"
        val requestBody = JSONObject()
            .put("amount", amount)
            .put("currency", "EUR")
            .put("source", "sdk_integration")
            .toString()
            .toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Authentication", "Bearer $token")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                completion(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        completion(Result.failure(IOException("Unexpected response: $response")))
                        return
                    }
                    
                    val jsonResponse = it.body?.string()
                    
                    try {
                        val dataObject = JSONObject(jsonResponse ?: "")
                        val redirectUrl = JSONObject(dataObject.get("data").toString()).get("redirectUrl").toString()
                        completion(Result.success(redirectUrl))
                    } catch (e: Exception) {
                        completion(Result.failure(e))
                    }
                }
            }
        })
    }
}
