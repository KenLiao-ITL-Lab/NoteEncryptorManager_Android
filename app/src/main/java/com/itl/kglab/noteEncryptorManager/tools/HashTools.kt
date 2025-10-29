package com.itl.kglab.noteEncryptorManager.tools

import android.util.Base64
import java.security.MessageDigest


class HashTools(
    private var settingInfo: SettingInfo
) {

    private val algorithmName: String = settingInfo.algorithmName.ifBlank {
        HashAlgorithmType.Sha256.algorithmName
    }

    private var messageDigest: MessageDigest = MessageDigest.getInstance(algorithmName)

    fun hashMessage(message: String): String {
        val hashArray = hash(message)
        val result = Base64.encodeToString(hashArray, Base64.URL_SAFE).apply {
            replace(Char(10), Char(40))
        }
        return result
    }


    private fun hash(message: String): ByteArray {
        messageDigest.update(message.toByteArray())

        val resultByte = messageDigest.digest()

        messageDigest.reset()

        return resultByte
    }



    companion object {
        fun getHashTypeList(): List<HashAlgorithmType> {
            return HashAlgorithmType.getList()
        }
    }
}