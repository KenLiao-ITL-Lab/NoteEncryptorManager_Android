package com.itl.kglab.noteEncryptorManager.repository

import com.itl.kglab.noteEncryptorManager.data.db.NoteInfoColumn
import com.itl.kglab.noteEncryptorManager.data.db.updateInfo.InfoSettingUpdate
import com.itl.kglab.noteEncryptorManager.tools.SettingInfo
import kotlinx.coroutines.flow.Flow

interface MainRepositoryInterface {
    suspend fun setSettingInfo(settingInfo: SettingInfo)
    fun getSettingInfo(): Flow<SettingInfo>

    suspend fun saveNoteInfo(info: NoteInfoColumn)
    suspend fun getNoteList(): List<NoteInfoColumn>
    suspend fun getNoteInfoById(id: Long): NoteInfoColumn
    suspend fun deleteNoteInfo(info: NoteInfoColumn)

    suspend fun saveNoteSampleSetting(info: InfoSettingUpdate)

    suspend fun clearPreviousPreferences()

}