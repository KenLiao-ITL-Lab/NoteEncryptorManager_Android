package com.itl.kglab.noteEncryptorManager.tools

data class SettingInfo(
    val algorithmName: String = "",
    val prefixText: String = "",
    val suffixText: String = "",
    val samplingSize: Int = 0,
    val sampleIndex: Int = 5
)

