package com.itl.kglab.noteEncryptorManager.ui.screen.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.itl.kglab.noteEncryptorManager.data.db.NoteInfo

class DetailViewState {

    private var _noteInfo by mutableStateOf(NoteInfo())
    val noteInfo get() = _noteInfo

    fun updateNoteInfo(note: NoteInfo) {
        _noteInfo = note
    }
}