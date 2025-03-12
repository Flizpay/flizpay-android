package org.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import flizpay2.flizpaysdk.FlizpaySDK
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class App : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlizpayPaymentScreen(this)
        }
    }

}

@Composable
fun FlizpayPaymentScreen(context: ComponentActivity) {
    // Put your API Key here, plus the API KEY shall be stored safely
    val backendURL = "http://192.168.2.34:8080"
    val testApiKey = "81d43faf756b3ad02f6eb2f4d193c92c8e9f8624522005035c48a9b740e5abd1"

    var userAmount by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    val iban by remember { mutableStateOf("BE85 7898 9842 8409 9034 8909") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Company X Nice APP")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = userAmount,
            onValueChange = { userAmount = it },
            label = { Text("Enter amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = userEmail,
            onValueChange = { userEmail = it },
            label = { Text("Enter email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = iban,
            onValueChange = {  val a = it },
            label = { Text("Enter Iban") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    val token = fetchToken(backendURL, testApiKey, userEmail, iban)
                    if (token != null) {
                        println("Received token: $token")
                        launchPayment(context, token, userAmount, userEmail)
                    } else {
                        println("Failed to fetch token")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().background(Color.Green)
        ) {
            Text("Pay with Fliz")
        }
    }
}

// This request should be performed in the backend, under your app authorization
private suspend fun fetchToken(backendURL: String, testApiKey: String, email: String, iban: String): String? {
    val payload = JSONObject()
        .put("email", email)
        .put("iban", iban)
        .toString()
        .toRequestBody("application/json".toMediaType())
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("${backendURL}/auth/verify-apikey")
        .post(payload)
        .addHeader("x-api-key", testApiKey)
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

private fun launchPayment(context: ComponentActivity, token: String, amount: String, email: String) {
    FlizpaySDK.initiatePayment(
        context,
        token,
        amount,
        email,
        keychainAccessKey = "key-for-keychain"
    )
}
