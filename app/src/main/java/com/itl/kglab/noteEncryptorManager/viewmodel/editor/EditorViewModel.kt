package com.itl.kglab.noteEncryptorManager.viewmodel.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itl.kglab.noteEncryptorManager.data.db.NoteInfo
import com.itl.kglab.noteEncryptorManager.repository.MainRepository
import com.itl.kglab.noteEncryptorManager.ui.data.NoteEventData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor (
    private val repository: MainRepository
) : ViewModel() {

    private val dateFormater = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.TAIWAN)

    var viewState = EditorViewState()
        private set

    fun updateNoteInfo(noteInfo: NoteInfo) {
        viewState.updateNoteInfo(noteInfo)
    }

    fun findNoteInfo(id: Long) {
        viewModelScope.launch {
            val info = repository.getNoteInfoById(id)
            viewState.updateNoteInfo(info)
        }
    }

    fun saveNoteInfo(evenData: NoteEventData) {
        val info = NoteInfo(
            id = viewState.noteInfo.id, // 必須帶入ID
            title = evenData.title,
            timeDesc = dateFormater.format(Date()),
            inputText = evenData.inputMessage,
            outputText = evenData.outputMessage,
            note = evenData.note,
            isPrivate = evenData.isPrivate
        )

        viewModelScope.launch {
            repository.saveNoteInfo(info)
        }
    }

}