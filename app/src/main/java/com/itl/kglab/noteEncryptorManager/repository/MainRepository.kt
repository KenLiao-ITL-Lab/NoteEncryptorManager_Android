package com.itl.kglab.noteEncryptorManager.repository

import com.itl.kglab.noteEncryptorManager.data.db.AppDatabase
import com.itl.kglab.noteEncryptorManager.data.db.NoteInfo
import com.itl.kglab.noteEncryptorManager.data.pref.PreferencesManager
import com.itl.kglab.noteEncryptorManager.tools.SettingInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainRepository @Inject constructor (
    private val preferencesManager: PreferencesManager,
    private val database: AppDatabase,
    private val ioDispatcher: CoroutineDispatcher
): MainRepositoryInterface {
    override suspend fun setSettingInfo(settingInfo: SettingInfo) {
        preferencesManager.setSettingInfo(settingInfo)
    }

    override fun getSettingInfo(): Flow<SettingInfo> {
        return preferencesManager.getSettingInfo()
    }

    override suspend fun saveNoteInfo(info: NoteInfo) =
        withContext(ioDispatcher) {
            val dao = database.noteInfoDao()
            dao.insertInfo(info)
        }

    override suspend fun getNoteList(): List<NoteInfo> {
        val dao = database.noteInfoDao()
        return dao.getInfoList()
    }
}