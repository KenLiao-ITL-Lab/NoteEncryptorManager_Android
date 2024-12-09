package com.itl.kglab.noteEncryptorManager.ui.screen.main.data

import com.itl.kglab.noteEncryptorManager.tools.SettingInfo

data class SettingScreenInfo(
    val algorithmIndex: Int = 0,
    val info: SettingInfo = SettingInfo()
)