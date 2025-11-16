package com.itl.kglab.noteEncryptorManager.viewmodel.detail

data class SampleSettingData(
    val id: Long = -1,
    val sampleIndex: String = "0",
    val sampleSize: String = "0",
    val input: String = "",
    val output: String = "",
    val sampledMessage: String = ""
)