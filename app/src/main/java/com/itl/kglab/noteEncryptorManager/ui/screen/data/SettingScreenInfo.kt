package com.itl.kglab.noteEncryptorManager.ui.screen.data

data class SettingScreenInfo(
    val algorithmName: String = "",
    val algorithmIndex: Int = 0,
    val prefixText: String = "",
    val suffixText: String = "",
    val samplingSize: Int = 0,
    val sampleIndex: Int = 0
)