package com.itl.kglab.noteEncryptorManager.repository

import com.itl.kglab.noteEncryptorManager.tools.SettingInfo
import kotlinx.coroutines.flow.Flow

interface MainRepositoryInterface {
    suspend fun setSettingInfo(settingInfo: SettingInfo)
    fun getSettingInfo(): Flow<SettingInfo>
}