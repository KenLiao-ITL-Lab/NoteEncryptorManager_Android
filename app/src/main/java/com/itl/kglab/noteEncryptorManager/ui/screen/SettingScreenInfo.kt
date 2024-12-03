package com.itl.kglab.noteEncryptorManager.ui.screen

data class SettingScreenInfo(
    val algorithmName: String,
    val prefixText: String,
    val suffixText: String,
    val samplingSize: Int,
    val sampleIndex: Int
)