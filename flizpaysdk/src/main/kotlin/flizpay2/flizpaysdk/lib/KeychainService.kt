package flizpay2.flizpaysdk.lib

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class KeychainService(context: Context, keychainAccessKey: String) {
    private val sharedPreferences: SharedPreferences

    init {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            keychainAccessKey,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun storeCredentials(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun fetchCredentials(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun clearCredentials(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }
}
