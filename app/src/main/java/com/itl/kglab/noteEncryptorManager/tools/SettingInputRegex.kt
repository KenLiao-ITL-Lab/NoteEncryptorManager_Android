package com.itl.kglab.noteEncryptorManager.tools

class SettingInputRegex {
    val decorateRegex = Regex("^.{0,10}\$")
    val sampleSizeRegex = Regex("^[0-9]\$")
    val sampleIndexRegex = Regex("^[0-9]{0,2}$")
}