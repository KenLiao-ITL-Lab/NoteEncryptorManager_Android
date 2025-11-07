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

    fun sampleMessage(
        message: String,
        index: Int,
        size: Int
    ): String {

        val builder = StringBuilder()

        // 取樣大小為0，則預設全部取樣
        var resultLength = if (size == 0) { message.length -1 } else { size - 1 }

        // 指標不為0，則取餘數
        var index = if (index != 0) { (index % (message.length)) } else { index }

        while (resultLength >= 0) {
            if (index >= message.length) index = 0

            // 跳過換行符號
            if (message[index].code != 10) {
                builder.append(message[index])
            }

            resultLength --
            index ++
        }

        return builder.toString()
    }



    companion object {
        fun getHashTypeList(): List<HashAlgorithmType> {
            return HashAlgorithmType.getList()
        }

        fun getAlgorithmListIndexByName(algorithmName: String): Int {
            val index = getHashTypeList().map { it.algorithmName }.indexOf(algorithmName)
            return if (index == -1) 0 else index
        }
    }
}

sealed class HashAlgorithmType(
    val algorithmName: String
) {

    data object Sha256 : HashAlgorithmType("SHA-256")
    data object Sha512 : HashAlgorithmType("SHA-512")

    companion object {
        fun getList(): List<HashAlgorithmType> {
            return listOf(
                Sha256,
                Sha512
            )
        }
    }
}