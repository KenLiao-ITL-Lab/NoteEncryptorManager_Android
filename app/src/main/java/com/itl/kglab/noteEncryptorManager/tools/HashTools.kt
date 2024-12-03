package com.itl.kglab.noteEncryptorManager.tools

import android.util.Base64
import java.security.MessageDigest


class HashTools(
    private var settingInfo: SettingInfo = SettingInfo()
) {

    private var messageDigest: MessageDigest = MessageDigest.getInstance(settingInfo.algorithm.algorithmName)

    fun hashMessage(message: String): String {
        val hashArray = hash(message)
        val result = Base64.encodeToString(hashArray, Base64.URL_SAFE).apply {
            replace(Char(10), Char(40))
        }
        return sampleMessage(result)
    }

    private fun sampleMessage(message: String): String {

        val builder = StringBuilder()

        // 取樣大小為0，則預設全部取樣
        var resultLength = if (settingInfo.samplingSize == 0) {
            message.length -1
        } else {
            settingInfo.samplingSize - 1
        }

        // 指標不為0，則取餘數
        var index = if (settingInfo.sampleIndex != 0) {
            (settingInfo.sampleIndex % (message.length))
        } else {
            settingInfo.sampleIndex
        }

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

    private fun hash(message: String): ByteArray {

        val builder = StringBuilder()

        builder.apply {
            append(settingInfo.prefixText)
            append(message)
            append(settingInfo.suffixText)
        }

        messageDigest.update(builder.toString().toByteArray())

        val resultByte = messageDigest.digest()

        messageDigest.reset()

        return resultByte
    }

    fun getHashTypeList(): List<HashAlgorithmType> {
        return HashAlgorithmType.getList()
    }

}