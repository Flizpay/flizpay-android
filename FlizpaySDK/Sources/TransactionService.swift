import Foundation

// TransactionResponse decodes the nested "redirectUrl" from within the "data" object.
public struct TransactionResponse: Codable {
    let redirectUrl: String?

    enum OuterKeys: String, CodingKey {
        case data
    }

    enum DataKeys: String, CodingKey {
        case redirectUrl
    }

    // Decoding from nested JSON
    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: OuterKeys.self)
        let dataContainer = try container.nestedContainer(keyedBy: DataKeys.self, forKey: .data)
        self.redirectUrl = try dataContainer.decode(String.self, forKey: .redirectUrl)
    }

    // Encoding into nested JSON (if needed)
    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: OuterKeys.self)
        var dataContainer = container.nestedContainer(keyedBy: DataKeys.self, forKey: .data)
        try dataContainer.encode(redirectUrl, forKey: .redirectUrl)
    }
}

// TransactionRequest is used to send the request data.
struct TransactionRequest: Codable {
    let amount: String
    let currency: String
    let source: String
}

// TransactionService makes the API call to fetch transaction info.
public class TransactionService {
    
    /// Calls the /transactions endpoint using the provided token and amount.
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
        
        // Create the transaction request with amount, currency "EUR", and source "sdk_integration"
        let body = TransactionRequest(amount: amount, currency: "EUR", source: "sdk_integration")
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
                print("Parsed Response:", transactionResponse)
                completion(.success(transactionResponse))
            } catch {
                completion(.failure(error))
            }
        }.resume()
    }
}
