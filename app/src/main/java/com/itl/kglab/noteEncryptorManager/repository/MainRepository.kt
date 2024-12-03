package com.itl.kglab.noteEncryptorManager.repository

import com.itl.kglab.noteEncryptorManager.data.PreferencesManager
import com.itl.kglab.noteEncryptorManager.tools.SettingInfo
import kotlinx.coroutines.flow.Flow

class MainRepository(
    private val preferencesManager: PreferencesManager
): MainRepositoryInterface {
    override suspend fun setSettingInfo(settingInfo: SettingInfo) {
        preferencesManager.setSettingInfo(settingInfo)
    }

    override fun getSettingInfo(): Flow<SettingInfo> {
        return preferencesManager.getSettingInfo()
    }
}