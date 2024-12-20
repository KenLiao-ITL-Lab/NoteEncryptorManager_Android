package com.itl.kglab.noteEncryptorManager.viewmodel.editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    var viewData by mutableStateOf(EditorViewData())
        private set

    fun updateViewData(
        data: EditorViewData
    ) {
        viewData = data
    }

    fun saveNoteInfo(evenData: NoteEventData) {
        val info = NoteInfo(
            id = evenData.id,
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