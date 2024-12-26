package com.itl.kglab.noteEncryptorManager.viewmodel.main

import com.itl.kglab.noteEncryptorManager.data.db.NoteInfo
import com.itl.kglab.noteEncryptorManager.ui.screen.main.data.SettingScreenInfo

data class ScreenState(
    val settingInfo: SettingScreenInfo = SettingScreenInfo(),
    val noteInfoList: List<NoteInfo> = emptyList(),
    val isLoading: Boolean = false
)