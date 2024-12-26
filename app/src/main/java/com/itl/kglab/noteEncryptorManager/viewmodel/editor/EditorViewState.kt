package com.itl.kglab.noteEncryptorManager.viewmodel.editor

import com.itl.kglab.noteEncryptorManager.data.db.NoteInfo

/**
 *  編輯頁面狀態
 */

data class EditorViewState(
    var noteInfo: NoteInfo, // Note 資料
    val isLoading: Boolean = false, // 讀取中
    val isDone: Boolean = false // 完成
)