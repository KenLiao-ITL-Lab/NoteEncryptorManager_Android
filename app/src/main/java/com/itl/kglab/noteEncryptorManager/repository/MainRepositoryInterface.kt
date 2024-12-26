package com.itl.kglab.noteEncryptorManager.repository

import com.itl.kglab.noteEncryptorManager.data.db.NoteInfo
import com.itl.kglab.noteEncryptorManager.tools.SettingInfo
import kotlinx.coroutines.flow.Flow

interface MainRepositoryInterface {
    suspend fun setSettingInfo(settingInfo: SettingInfo)
    fun getSettingInfo(): Flow<SettingInfo>

    suspend fun saveNoteInfo(info: NoteInfo)
    suspend fun getNoteList(): List<NoteInfo>
    suspend fun getNoteInfoById(id: Long): NoteInfo
    suspend fun deleteNoteInfo(info: NoteInfo)

}