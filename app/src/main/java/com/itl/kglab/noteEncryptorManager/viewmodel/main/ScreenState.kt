package com.itl.kglab.noteEncryptorManager.viewmodel.main

import com.itl.kglab.noteEncryptorManager.data.db.NoteInfoColumn
import com.itl.kglab.noteEncryptorManager.ui.screen.main.data.SettingScreenInfo

data class ScreenState(
    val settingInfo: SettingScreenInfo = SettingScreenInfo(),
    val noteInfoList: List<NoteInfoColumn> = emptyList(),
    val isLoading: Boolean = false
)