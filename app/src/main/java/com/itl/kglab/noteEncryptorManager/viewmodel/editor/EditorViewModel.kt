package com.itl.kglab.noteEncryptorManager.viewmodel.editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.itl.kglab.noteEncryptorManager.ui.data.NoteEventData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor () : ViewModel() {

    var viewData by mutableStateOf(EditorViewData())
        private set

    fun updateViewData(
        data: EditorViewData
    ) {
        viewData = data
    }

    fun saveNoteInfo(note: NoteEventData) {

    }

}