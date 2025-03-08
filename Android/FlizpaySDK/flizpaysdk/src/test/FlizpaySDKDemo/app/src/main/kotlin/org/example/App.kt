package com.example.flizpaysdkdemo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.flizpay2.flizpaysdk.FlizpaySDK
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class App : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlizpayPaymentScreen()
        }
    }
}

@Composable
fun FlizpayPaymentScreen() {
    var userAmount by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = userAmount,
            onValueChange = { userAmount = it },
            label = { Text("Enter amount") },
            keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = userEmail,
            onValueChange = { userEmail = it },
            label = { Text("Enter email") },
            keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    val token = fetchToken()
                    if (token != null) {
                        println("Received token: $token")
                        launchPayment(token, userAmount, userEmail)
                    } else {
                        println("Failed to fetch token")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pay with Fliz")
        }
    }
}

private suspend fun fetchToken(): String? {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("http://10.10.111.130:8080/auth/verify-apikey")
        .post(RequestBody.create(null, ByteArray(0)))  // Empty POST request
        .addHeader("x-api-key", "0413bfa6c2ec433350c5eab97ec34f8ac6ca133c83680913e3a592296eb99171")
        .build()

    return withContext(Dispatchers.IO) {
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    println("Failed request: ${response.message}")
                    return@withContext null
                }
                response.body?.string()?.let { responseBody ->
                    val json = JSONObject(responseBody)
                    return@withContext json.getJSONObject("data").getString("token")
                }
            }
        } catch (e: IOException) {
            println("Network error: ${e.message}")
            null
        }
    }
}

private fun launchPayment(token: String, amount: String, email: String) {
    val activity = FlizpaySDK.instance.getCurrentActivity()
    activity?.let {
        FlizpaySDK.initiatePayment(
            it,
            token,
            amount,
            email
        ) { error ->
            println("Payment failed: ${error.message}")
        }
    }
}
