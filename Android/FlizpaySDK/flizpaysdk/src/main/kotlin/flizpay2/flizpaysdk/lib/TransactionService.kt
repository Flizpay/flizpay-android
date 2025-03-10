package flizpay2.flizpaysdk.lib

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import okhttp3.*
import java.io.IOException

import flizpay2.flizpaysdk.Constants
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

// TransactionResponse decodes the nested "redirectUrl" from within the "data" object.
data class TransactionResponse(
    @SerializedName("redirectUrl") val redirectUrl: String?
)

// TransactionRequest is used to send the request data.
data class TransactionRequest(
    val amount: String,
    val currency: String = "EUR",
    val source: String = "sdk_integration"
)

class TransactionService {
    private val client = OkHttpClient()
    private val gson = Gson()

    /**
     * Calls the /transactions endpoint using the provided token and amount.
     */
    fun fetchTransactionInfo(
        token: String,
        amount: String,
        completion: (Result<TransactionResponse>) -> Unit
    ) {
        val url = "${Constants.API_URL}/transactions"
        val requestBody = gson.toJson(TransactionRequest(amount))
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
                    Log.d("TransactionService", "Raw response JSON: $jsonResponse")
                    
                    try {
                        val dataObject = gson.fromJson(jsonResponse, JsonObject::class.java)
                        val redirectUrl = dataObject["data"]?.asJsonObject?.get("redirectUrl")?.asString
                        completion(Result.success(TransactionResponse(redirectUrl)))
                    } catch (e: Exception) {
                        completion(Result.failure(e))
                    }
                }
            }
        })
    }
}
