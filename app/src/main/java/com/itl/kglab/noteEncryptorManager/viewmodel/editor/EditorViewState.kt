package com.itl.kglab.noteEncryptorManager.viewmodel.editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.itl.kglab.noteEncryptorManager.data.db.NoteInfoColumn

/**
 *  編輯頁面狀態
 */

class EditorViewState {
    //     var noteInfo: NoteInfo, // Note 資料
    //    val isLoading: Boolean = false, // 讀取中
    //    val isDone: Boolean = false // 完成

    private var _noteInfo by mutableStateOf(NoteInfoColumn())
    val noteInfo get() = _noteInfo

    fun updateNoteInfo(note: NoteInfoColumn) {
        _noteInfo = note
    }
}