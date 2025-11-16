package com.itl.kglab.noteEncryptorManager.data.pref

import androidx.datastore.core.Serializer
import com.itl.kglab.noteEncryptorManager.tools.CryptographicUtility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import java.util.Base64

@Serializable
data class SettingPreferences(
    val algorithm: String = "",
    val prefix: String = "",
    val suffix: String = ""
)

object SettingPreferencesSerializer : Serializer<SettingPreferences> {
    override val defaultValue: SettingPreferences
        get() = SettingPreferences()

    override suspend fun readFrom(input: InputStream): SettingPreferences {
        val encryptedBytes = withContext(Dispatchers.IO) {
            input.use { it.readBytes() }
        }
        val encryptedBytesDecoded = Base64.getDecoder().decode(encryptedBytes)
        val decryptedBytes = CryptographicUtility.decrypt(encryptedBytesDecoded)
        val decodedJsonString = decryptedBytes.decodeToString()
        return Json.decodeFromString(decodedJsonString)
    }

    override suspend fun writeTo(
        t: SettingPreferences,
        output: OutputStream
    ) {
        val json = Json.encodeToString(t)
        val bytes = json.toByteArray()
        val encryptedBytes = CryptographicUtility.encrypt(bytes)
        val encryptedBytesBase64 = Base64.getEncoder().encode(encryptedBytes)
        withContext(Dispatchers.IO) {
            output.use {
                it.write(encryptedBytesBase64)
            }
        }
    }
}