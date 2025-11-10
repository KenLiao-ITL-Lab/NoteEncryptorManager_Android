package com.itl.kglab.noteEncryptorManager.tools

object SettingInputRegex {
    val decorateRegex = Regex("^.{0,10}\$")
    val sampleSizeInputRegex = Regex("^[0-9]?$")
    val sampleIndexInputRegex = Regex("^[0-9]{0,2}$")

    val sampleSizeCheckRegex = Regex("[1-9][0-9]?$")
    val sampleIndexCheckRegex = Regex("^[1-9][0-9]?$")

    const val DEFAULT_SIZE: Int = 8
    const val DEFAULT_INDEX: Int = 0
}