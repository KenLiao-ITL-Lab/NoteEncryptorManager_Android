package com.itl.kglab.noteEncryptorManager.ui.data

import com.itl.kglab.noteEncryptorManager.data.db.NoteInfoColumn

data class DeleteNoteDataEvent(
    val noteInfo: NoteInfoColumn? = null,
    val isShowConfirmDialog: Boolean = false
)