# FLIZpay Android SDK

[![Platform](https://img.shields.io/badge/platform-Android-green)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/kotlin-1.9.0-purple)](https://kotlinlang.org/)
[![Gradle Compatible](https://img.shields.io/badge/Gradle-compatible-brightgreen)](https://gradle.org/)
[![Version](https://img.shields.io/github/v/tag/flizpay/flizpay-android)](https://github.com/flizpay/flizpay-android/releases)
[![](https://jitpack.io/v/flizpay/flizpay-android.svg)](https://jitpack.io/#flizpay/flizpay-android)
[![License](https://img.shields.io/github/license/flizpay/flizpay-android)](LICENSE)
[![Coverage Status](https://coveralls.io/repos/github/Flizpay/flizpay-android/badge.svg?branch=feat/add-test-coverage)](https://coveralls.io/github/Flizpay/flizpay-android?branch=feat/add-test-coverage)

Welcome to the FLIZpay Android SDK! Easily integrate secure, seamless, and user-friendly payments directly into your Android app.

## 🚀 Overview

The FLIZpay SDK simplifies accepting payments by managing the entire payment flow via an integrated webview, securely and intuitively within your app.

Get started with our 📚 [integration guide](https://www.docs.flizpay.de/docs/sdk/Installation)

---

## 📦 Requirements

The FLIZpay Android SDK requires Android 7 or later and is compatible with apps targeting Android 21 or above.

## ⚡️ Quick Start

After installing the SDK, configure a callback scheme in your app and initiate payments:

```kotlin
import flizpay2.flizpaysdk.FlizpaySDK

FlizpaySDK.initiatePayment(
    context = context,
    token = token,
    amount = amount,
    metadata = mapOf(
        "orderId" to "order-123",
        "customerId" to "customer-456"
    ),
    urlScheme = "myapp://flizpay-return",
    onFailure = { error ->
        // Handle SDK or transaction setup errors
    }
)
```

In your app module, make sure the callback scheme matches the one passed to `urlScheme`:

```kotlin
android {
    defaultConfig {
        manifestPlaceholders["flizpayUrlScheme"] = "myapp"
    }
}
```

### Parameters

- **`context`** (`ComponentActivity`, required): The activity of which to launch the webview from
- **`amount`** (`String`, required): The payment amount.
- **`token`** (`String`, required): JWT authentication token obtained from your backend.
- **`metadata`** (`Map<String, Any?>`, optional): Additional metadata forwarded when creating the transaction.
- **`urlScheme`** (`String`, required): The host app callback URL used when redirect-based bank authorization returns the user to your app.
- **`onFailure`** (`((Throwable) -> Unit)`, optional): Callback to be executed on failure.

### Redirect-based Bank Flows

For redirect-based banks such as Revolut or ING, the SDK passes `urlScheme` to FLIZpay payer-web application so the bank app can return the user to the app that owns the WebView after authorization.

The scheme configured in `manifestPlaceholders["flizpayUrlScheme"]` must match the scheme part of the `urlScheme` value you pass to `FlizpaySDK.initiatePayment(...)`.

---

## 📖 Detailed Integration Guide

For comprehensive integration details, API authentication steps, obtaining JWT tokens, and additional examples, see our [Integration Documentation](https://www.docs.flizpay.de/docs/sdk/Installation).

---

## 📄 License

FLIZpay SDK is available under the MIT license. See the [LICENSE](LICENSE) file for more details.

---

## 🛟 Support

Need assistance? Our support team is here to help.

👉 [Contact FLIZpay Support](https://support.flizpay.de)

---

Happy coding! 🚀🎉
