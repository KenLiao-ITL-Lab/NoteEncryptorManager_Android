package com.itl.kglab.noteEncryptorManager.ui.data

import com.itl.kglab.noteEncryptorManager.viewmodel.main.NoteInfoListInfoData

data class DeleteNoteDataEvent(
    val noteInfo: NoteInfoListInfoData? = null,
    val isShowConfirmDialog: Boolean = false
)