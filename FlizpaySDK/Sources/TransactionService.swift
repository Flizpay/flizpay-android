import Foundation

// Updated TransactionResponse with an optional redirectUrl
public struct TransactionResponse: Codable {
    let redirectUrl: String?
}

// Include currency in the transaction request
struct TransactionRequest: Codable {
    let amount: String
    let currency: String
    let source: String
}

public class TransactionService {
    
    /// Calls your /transactions endpoint, using the **passed token** from the host app.
    public func fetchTransactionInfo(
        token: String,
        amount: String,
        completion: @escaping (Result<TransactionResponse, Error>) -> Void
    ) {
        guard let url = URL(string: "http://10.10.111.130:8080/transactions") else {
            completion(.failure(NSError(domain: "Invalid URL", code: -1, userInfo: nil)))
            return
        }
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        
        // Set the token in the header as expected by your backend.
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authentication")
        
        // Set JSON content type.
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        // Create the transaction request with amount, currency "EUR", and source "webview"
        let body = TransactionRequest(amount: amount, currency: "EUR", source: "webview")
        do {
            let jsonData = try JSONEncoder().encode(body)
            request.httpBody = jsonData
        } catch {
            completion(.failure(error))
            return
        }
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                completion(.failure(error))
                return
            }
            guard let data = data else {
                completion(.failure(NSError(domain: "No Data", code: -1, userInfo: nil)))
                return
            }
            
            // Debug: Print raw response JSON.
            if let rawJson = String(data: data, encoding: .utf8) {
                print("Raw response JSON: \(rawJson)")
            }
            
            do {
                let transactionResponse = try JSONDecoder().decode(TransactionResponse.self, from: data)
                print("after here")
                completion(.success(transactionResponse))
            } catch {
                completion(.failure(error))
            }
        }.resume()
    }
}
