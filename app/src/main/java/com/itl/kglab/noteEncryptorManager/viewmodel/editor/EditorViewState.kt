package com.itl.kglab.noteEncryptorManager.viewmodel.editor


/**
 *  編輯頁面狀態
 */

data class EditorViewState(
    val data: EditorViewData = EditorViewData(), // Note 資料
    val isLoading: Boolean = false, // 讀取中
    val isDone: Boolean = false // 完成
)