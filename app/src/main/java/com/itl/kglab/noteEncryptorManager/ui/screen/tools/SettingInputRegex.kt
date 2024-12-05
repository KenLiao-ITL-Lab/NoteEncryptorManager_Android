package com.itl.kglab.noteEncryptorManager.ui.screen.tools

class SettingInputRegex {
    val decorateRegex = Regex("^.{0,10}\$")
    val sampleSizeRegex = Regex("^[0-9 ]?\$")
    val indexRegex = Regex("^[0-9 ]{0,2}$")
}