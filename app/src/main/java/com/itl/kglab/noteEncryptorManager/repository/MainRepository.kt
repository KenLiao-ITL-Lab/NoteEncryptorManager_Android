package com.itl.kglab.noteEncryptorManager.repository

import com.itl.kglab.noteEncryptorManager.data.db.NoteInfoColumn
import com.itl.kglab.noteEncryptorManager.data.db.NoteInfoDao
import com.itl.kglab.noteEncryptorManager.data.db.updateInfo.InfoSettingUpdate
import com.itl.kglab.noteEncryptorManager.data.db.updateInfo.NoteInfoUpdate
import com.itl.kglab.noteEncryptorManager.data.pref.PreferencesManager
import com.itl.kglab.noteEncryptorManager.tools.CryptographicUtility
import com.itl.kglab.noteEncryptorManager.tools.SettingInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.io.encoding.Base64

class MainRepository @Inject constructor (
    private val preferencesManager: PreferencesManager,
    private val noteInfoDao: NoteInfoDao,
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

    override suspend fun createNoteInfo(info: NoteInfoColumn) =
        withContext(ioDispatcher) {
            noteInfoDao.insertInfo(encryptInfoColumn(info))
        }

    override suspend fun getNoteList(): List<NoteInfoColumn> =
        withContext(ioDispatcher) {
            noteInfoDao.getInfoList().map { decryptInfoColumn(it) }
        }

    override suspend fun getNoteInfoById(id: Long): NoteInfoColumn =
        withContext(ioDispatcher) {
            decryptInfoColumn(noteInfoDao.getInfoById(id))
        }

    override suspend fun deleteNoteInfo(info: NoteInfoColumn) =
        withContext(ioDispatcher) {
            noteInfoDao.deleteInfo(info)
        }

    override suspend fun deleteNoteById(id: Long) {
        withContext(ioDispatcher) {
            noteInfoDao.deleteInfoById(id)
        }
    }

    override suspend fun updateNoteInfo(info: NoteInfoUpdate) {
        withContext(ioDispatcher) {
            val encryptedInfo = info.copy(
                note = encryptMessage(info.note)
            )
            noteInfoDao.updateNoteInfo(encryptedInfo)
        }
    }

    override suspend fun updateNoteSampleSetting(info: InfoSettingUpdate) {
        withContext(ioDispatcher) {
            val encryptedInfo = info.copy(
                inputText = encryptMessage(info.inputText),
                outputText = encryptMessage(info.outputText),
                sampledMessage = encryptMessage(info.sampledMessage)
            )
            noteInfoDao.updateInfoSetting(encryptedInfo)
        }
    }

    /**
     *  加解密Func
     */
    private fun encryptInfoColumn(info: NoteInfoColumn): NoteInfoColumn {
        return info.copy(
            note = encryptMessage(info.note),
            inputText = encryptMessage(info.inputText),
            outputText = encryptMessage(info.outputText),
            sampledMessage = encryptMessage(info.sampledMessage)
        )
    }

    private fun decryptInfoColumn(encryptedInfo: NoteInfoColumn): NoteInfoColumn {
        return encryptedInfo.copy(
            note = decryptMessage(encryptedInfo.note),
            inputText = decryptMessage(encryptedInfo.inputText),
            outputText = decryptMessage(encryptedInfo.outputText),
            sampledMessage = decryptMessage(encryptedInfo.sampledMessage)
        )
    }

    private fun encryptMessage(message: String): String {
        val encryptArray = CryptographicUtility.encrypt(message.toByteArray())
        return Base64.encode(encryptArray)
    }

    private fun decryptMessage(message: String): String {
        val encryptedArray = Base64.decode(message)
        return String(CryptographicUtility.decrypt(encryptedArray))
    }
}