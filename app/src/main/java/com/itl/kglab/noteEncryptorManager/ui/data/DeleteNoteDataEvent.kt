package com.itl.kglab.noteEncryptorManager.ui.data

import com.itl.kglab.noteEncryptorManager.data.db.NoteInfo

data class DeleteNoteDataEvent(
    val noteInfo: NoteInfo? = null,
    val isShowConfirmDialog: Boolean = false
)