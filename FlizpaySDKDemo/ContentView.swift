import SwiftUI
import FlizpaySDK

// Codable structs to model the backend's JSON response.
struct AuthResponse: Codable {
    let message: String
    let data: TokenData
}

struct TokenData: Codable {
    let token: String
}

struct ContentView: View {
    // State variable for the user's input amount.
    @State private var userAmount: String = ""

    var body: some View {
        VStack {
            // A TextField for the user to enter the payment amount.
            TextField("Enter amount", text: $userAmount)
                .keyboardType(.decimalPad)
                .padding()
                .textFieldStyle(RoundedBorderTextFieldStyle())

            // A button to trigger the payment flow.
            Button("Pay with Fliz") {
                // 1. Fetch the token from the backend.
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
                            
                            PaymentController.initiatePayment(
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
    
    /// Calls your backend to fetch the JWT token.
    private func fetchToken(completion: @escaping (String?) -> Void) {
        guard let url = URL(string: "http://10.10.111.130:8080/auth/verify-apikey") else {
            completion(nil)
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue(
            "9aaa8134521b77a14a7d1e0db91149eca1fc47e567fb9f4b0330eaa591cded79",
            forHTTPHeaderField: "x-api-key"
        )
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            guard let data = data, error == nil else {
                completion(nil)
                return
            }
            
            do {
                // Decode the JSON response.
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


