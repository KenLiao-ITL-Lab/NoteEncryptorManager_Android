package com.itl.kglab.noteEncryptorManager.viewmodel.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itl.kglab.noteEncryptorManager.data.db.NoteInfoColumn
import com.itl.kglab.noteEncryptorManager.data.db.updateInfo.NoteInfoUpdate
import com.itl.kglab.noteEncryptorManager.repository.MainRepository
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

    fun setNewInfo(
        input: String,
        output: String
    ) {
        viewState.setNewInfo(
            input = input,
            output = output
        )
    }

    fun updateNoteInfo(
        title: String,
        note: String
    ) {
        viewState.updateNoteInfo(
            title = title,
            note = note
        )
    }

    fun findNoteInfo(id: Long) {
        viewModelScope.launch {
            val info = repository.getNoteInfoById(id)
            viewState.initNoteInfo(
                EditorNoteInfoData(
                    id = info.id,
                    title = info.title,
                    note = info.note,
                    input = info.inputText,
                    output = info.outputText,
                    isPrivate = info.isPrivate
                )
            )
        }
    }

    fun saveNoteInfo() {
        val info = viewState.noteInfo

        viewModelScope.launch {
            if (info.id == 0L) {
                repository.createNoteInfo(
                    NoteInfoColumn(
                        id = 0,
                        title = info.title,
                        note = info.note,
                        timeDesc = dateFormater.format(Date()),
                        isPrivate = info.isPrivate,
                        inputText = info.input,
                        outputText = info.output
                    )
                )
            } else {
                repository.updateNoteInfo(
                    NoteInfoUpdate(
                        id = info.id,
                        title = info.title,
                        timeDesc = info.time,
                        note = info.note,
                        isPrivate = info.isPrivate
                    )
                )
            }

        }
    }

}