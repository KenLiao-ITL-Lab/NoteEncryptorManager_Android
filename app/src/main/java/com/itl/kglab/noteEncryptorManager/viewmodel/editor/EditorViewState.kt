package com.itl.kglab.noteEncryptorManager.viewmodel.editor

sealed class EditorViewState {

    data object Loading : EditorViewState()
    data class UpdateViewData(
        val data: EditorViewData
    ) : EditorViewState()

    data object Complete : EditorViewState()
}