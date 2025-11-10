package com.itl.kglab.noteEncryptorManager.repository

import com.itl.kglab.noteEncryptorManager.data.db.AppDatabase
import com.itl.kglab.noteEncryptorManager.data.db.NoteInfoColumn
import com.itl.kglab.noteEncryptorManager.data.db.updateInfo.InfoSettingUpdate
import com.itl.kglab.noteEncryptorManager.data.db.updateInfo.NoteInfoUpdate
import com.itl.kglab.noteEncryptorManager.data.pref.PreferencesManager
import com.itl.kglab.noteEncryptorManager.tools.SettingInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainRepository @Inject constructor (
    private val preferencesManager: PreferencesManager,
    private val database: AppDatabase,
    private val ioDispatcher: CoroutineDispatcher
): MainRepositoryInterface {

    override suspend fun clearPreviousPreferences() {
        preferencesManager.clearLastVersionKeys()
    }

    override suspend fun setSettingInfo(settingInfo: SettingInfo) {
        preferencesManager.setSettingInfo(settingInfo)
    }

    override fun getSettingInfo(): Flow<SettingInfo> {
        return preferencesManager.getSettingInfo()
    }

    override suspend fun saveNoteInfo(info: NoteInfoColumn) =
        withContext(ioDispatcher) {
            val dao = database.noteInfoDao()
            dao.insertInfo(info)
        }

    override suspend fun getNoteList(): List<NoteInfoColumn> =
        withContext(ioDispatcher) {
            val dao = database.noteInfoDao()
            dao.getInfoList()
        }

    override suspend fun getNoteInfoById(id: Long): NoteInfoColumn =
        withContext(ioDispatcher) {
            val dao = database.noteInfoDao()
            dao.getInfoById(id)
        }

    override suspend fun deleteNoteInfo(info: NoteInfoColumn) =
        withContext(ioDispatcher) {
            val dao = database.noteInfoDao()
            dao.deleteInfo(info)
        }

    override suspend fun updateNoteInfo(info: NoteInfoUpdate) {
        withContext(ioDispatcher) {
            val dao = database.noteInfoDao()
            dao.updateNoteInfo(info)
        }
    }

    override suspend fun updateNoteSampleSetting(info: InfoSettingUpdate) {
        withContext(ioDispatcher) {
            val dao = database.noteInfoDao()
            dao.updateInfoSetting(info)
        }
    }
}