import SwiftUI
import FlizpaySDK

// MARK: - Constants

private let flizpayApiKey = "4b7e82cc1bcb7c1f1bfbc577f65c22402ce9d4e9890181a3009650fced99fea4"
private let verifyApiKeyURLString = "http://10.10.111.130:8080/auth/verify-apikey"

// MARK: - Models

/// Represents the response structure returned by your backend when verifying the API key.
struct AuthResponse: Codable {
    let message: String
    let data: TokenData
}

/// Contains the token data extracted from the backend's response.
struct TokenData: Codable {
    let token: String
}

// MARK: - ContentView

struct ContentView: View {
    
    // MARK: - UI State
    
    /// Payment amount entered by the user.
    @State private var userAmount: String = ""
    /// User's email address for the payment flow.
    @State private var userEmail: String = ""
    /// User's IBAN for the payment flow.
    @State private var userIban: String = ""
    
    // MARK: - Body
    
    var body: some View {
        VStack(spacing: 16) {
            
            // A TextField for the user to enter the payment amount.
            TextField("Enter amount", text: $userAmount)
                .keyboardType(.decimalPad)
                .padding()
                .textFieldStyle(RoundedBorderTextFieldStyle())
            
            // A TextField for the user to enter their email.
            TextField("Enter email", text: $userEmail)
                .keyboardType(.emailAddress)
                .autocapitalization(.none)
                .disableAutocorrection(true)
                .padding()
                .textFieldStyle(RoundedBorderTextFieldStyle())
            
            // A TextField for the user to enter their IBAN.
            TextField("Enter IBAN", text: $userIban)
                .autocapitalization(.none)
                .disableAutocorrection(true)
                .padding()
                .textFieldStyle(RoundedBorderTextFieldStyle())
            
            // A button to trigger the payment flow.
            Button("Pay with Fliz") {
                // 1. Fetch the token from the backend, including the email and IBAN in the request body.
                fetchToken { token in
                    guard let token = token else {
                        print("Failed to fetch token")
                        return
                    }
                    print("Received token: \(token)")
                    
                    // 2. Use the token to initiate the payment flow.
                    DispatchQueue.main.async {
                        if let scene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
                           let rootVC = scene.windows.first?.rootViewController {
                            FlizpaySDK.initiatePayment(
                                from: rootVC,
                                token: token,
                                amount: userAmount
                            ) { error in
                                // Handle any error returned from the SDK.
                                print("Payment failed: \(error)")
                            }
                        }
                    }
                }
            }
            .padding()
        }
        .padding()
    }
    
    // MARK: - Networking
    
    /// Calls your backend to fetch the JWT token, passing `payerEmail` and `payerIban` in the request body.
    private func fetchToken(completion: @escaping (String?) -> Void) {
        guard let url = URL(string: verifyApiKeyURLString) else {
            completion(nil)
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        
        // Set the necessary headers.
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue(flizpayApiKey, forHTTPHeaderField: "x-api-key")
        
        // Include payerEmail and payerIban in the request body.
        let requestBody: [String: Any] = [
            "payerEmail": userEmail,
            "payerIban": userIban
        ]
        
        // Convert the dictionary to JSON data.
        request.httpBody = try? JSONSerialization.data(withJSONObject: requestBody, options: [])
        
        // Perform the network request.
        URLSession.shared.dataTask(with: request) { data, response, error in
            // Handle errors or missing data.
            guard let data = data, error == nil else {
                completion(nil)
                return
            }
            
            // Attempt to decode the JSON response.
            do {
                let authResponse = try JSONDecoder().decode(AuthResponse.self, from: data)
                let token = authResponse.data.token
                print("Received token: \(token)")
                completion(token)
            } catch {
                print("Decoding error: \(error)")
                completion(nil)
            }
        }.resume()
    }
}
