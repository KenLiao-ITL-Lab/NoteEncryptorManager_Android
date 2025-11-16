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

    private var _noteInfo by mutableStateOf(EditorNoteInfoData())
    val noteInfo get() = _noteInfo

    fun initNoteInfo(
        noteInfoData: EditorNoteInfoData
    ) {
        _noteInfo = noteInfoData
    }

    fun setNewInfo(
        input: String,
        output: String,
    ) {
        _noteInfo = _noteInfo.copy(
            id = 0,
            input = input,
            output = output
        )
    }

    fun updateNoteInfo(
        title: String,
        note: String
    ) {
        _noteInfo = _noteInfo.copy(
            title = title,
            note = note
        )
    }
}