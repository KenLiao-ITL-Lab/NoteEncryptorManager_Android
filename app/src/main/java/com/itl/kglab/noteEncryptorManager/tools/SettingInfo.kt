package com.itl.kglab.noteEncryptorManager.tools

data class SettingInfo(
    val algorithm: HashAlgorithmType = HashAlgorithmType.Sha256,
    val prefix: String = "",
    val suffix: String = "",
    val samplingSize: Int = 0,
    val sampleIndex: Int = 5
)

