# FLIZpay Android SDK

[![Platform](https://img.shields.io/badge/platform-Android-green)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/kotlin-1.9.0-purple)](https://kotlinlang.org/)
[![Gradle Compatible](https://img.shields.io/badge/Gradle-compatible-brightgreen)](https://gradle.org/)
[![Version](https://img.shields.io/github/v/tag/yourusername/FLIZpayAndroidSDK)](https://github.com/yourusername/FLIZpayAndroidSDK/releases)
[![License](https://img.shields.io/github/license/yourusername/FLIZpayAndroidSDK)](LICENSE)


Welcome to the FLIZpay Android SDK! Easily integrate secure, seamless, and user-friendly payments directly into your Android app.

## 🚀 Overview

The FLIZpay SDK simplifies accepting payments by managing the entire payment flow via an integrated webview, securely and intuitively within your app.

Get started with our 📚 [integration guide](https://www.docs.flizpay.de/docs/sdk/Installation)

---

## 📦 Requirements

The FLIZpay Android SDK requires Android 7 or later and is compatible with apps targeting Android 21 or above. 

## ⚡️ Quick Start

After installing the SDK, initiate payments effortlessly:

```swift
import FlizpaySDK

FlizpaySDK.initiatePayment(amount: "49.99", token: "YOUR_JWT_TOKEN")
```

### Parameters

- **`amount`** (`String`, required): The payment amount.
- **`token`** (`String`, optional): JWT authentication token obtained from your backend.

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
