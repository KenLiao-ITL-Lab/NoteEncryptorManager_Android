package com.itl.kglab.noteEncryptorManager.repository

import com.itl.kglab.noteEncryptorManager.data.db.AppDatabase
import com.itl.kglab.noteEncryptorManager.data.db.NoteInfo
import com.itl.kglab.noteEncryptorManager.data.pref.PreferencesManager
import com.itl.kglab.noteEncryptorManager.tools.SettingInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MainRepository @Inject constructor (
    private val preferencesManager: PreferencesManager,
    private val database: AppDatabase
): MainRepositoryInterface {
    override suspend fun setSettingInfo(settingInfo: SettingInfo) {
        preferencesManager.setSettingInfo(settingInfo)
    }

    override fun getSettingInfo(): Flow<SettingInfo> {
        return preferencesManager.getSettingInfo()
    }

    override suspend fun saveNoteInfo(info: NoteInfo) {
        val dao = database.noteInfoDao()
        dao.insertInfo(info)
    }

    override suspend fun getNoteList(): List<NoteInfo> {
        val dao = database.noteInfoDao()
        return dao.getInfoList()
    }
}