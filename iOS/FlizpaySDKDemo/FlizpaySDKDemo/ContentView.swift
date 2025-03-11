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
  // State variables for the user’s inputs.
  @State private var userAmount: String = ""
  @State private var userEmail: String = ""
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
              FlizpaySDK.initiatePayment(
                from: rootVC,
                token: token,
                amount: userAmount,
                email: userEmail // <-- Pass the email to the payment controller
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
      "0413bfa6c2ec433350c5eab97ec34f8ac6ca133c83680913e3a592296eb99171",
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
