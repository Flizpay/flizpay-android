package flizpay2.flizpaysdk

object Constants {
    var API_URL: String = "https://api.flizpay.de"
    var BASE_URL: String = "https://secure.flizpay.de"
    var URL_SCHEME: String = "flizpaywebview://"
    var NO_CREDS_BANKS = listOf(
        "myaccount.ing.com",    // ING-DiBa
        "revolut.com",    // Revolut
        "consorsbank.de", // Consorsbank
        "n26.com",        // N26
        "tomorrow.one",   // Tomorrow
        "kontist.com",    // Kontist
        "finom.com"       // Finom
    )
}
