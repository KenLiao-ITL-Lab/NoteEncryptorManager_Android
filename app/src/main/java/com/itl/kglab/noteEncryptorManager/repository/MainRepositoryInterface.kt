package com.itl.kglab.noteEncryptorManager.repository

import com.itl.kglab.noteEncryptorManager.data.db.NoteInfoColumn
import com.itl.kglab.noteEncryptorManager.data.db.updateInfo.InfoSettingUpdate
import com.itl.kglab.noteEncryptorManager.data.db.updateInfo.NoteInfoUpdate
import com.itl.kglab.noteEncryptorManager.tools.SettingInfo
import kotlinx.coroutines.flow.Flow

interface MainRepositoryInterface {
    suspend fun setSettingInfo(settingInfo: SettingInfo)
    fun getSettingInfo(): Flow<SettingInfo>

    suspend fun createNoteInfo(info: NoteInfoColumn)
    suspend fun getNoteList(): List<NoteInfoColumn>
    suspend fun getNoteInfoById(id: Long): NoteInfoColumn
    suspend fun deleteNoteInfo(info: NoteInfoColumn)
    suspend fun deleteNoteById(id: Long)

    suspend fun clearPreviousPreferences()

    suspend fun updateNoteInfo(info: NoteInfoUpdate)
    suspend fun updateNoteSampleSetting(info: InfoSettingUpdate)

}