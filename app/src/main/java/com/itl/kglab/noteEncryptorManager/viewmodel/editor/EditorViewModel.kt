package com.itl.kglab.noteEncryptorManager.viewmodel.editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itl.kglab.noteEncryptorManager.data.db.NoteInfoColumn
import com.itl.kglab.noteEncryptorManager.data.db.updateInfo.NoteInfoUpdate
import com.itl.kglab.noteEncryptorManager.repository.MainRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val repository: MainRepositoryInterface
) : ViewModel() {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.TAIWAN)
    private var isInitialized = false

    var viewState = EditorViewState()
        private set

    var isSaving by mutableStateOf(false)
        private set

    var isAppBiometricEnabled by mutableStateOf(false)
        private set

    private val saveCompletedChannel = Channel<Unit>(Channel.BUFFERED)
    val saveCompleted = saveCompletedChannel.receiveAsFlow()

    init {
        refreshBiometricSetting()
    }

    fun refreshBiometricSetting() {
        viewModelScope.launch {
            isAppBiometricEnabled = repository.getSettingInfo().first().isBiometricEnabled
        }
    }

    fun setNewInfoIfNeeded(
        input: String,
        output: String
    ) {
        if (isInitialized) return
        isInitialized = true
        setNewInfo(input = input, output = output)
    }

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
        note: String,
        isPrivate: Boolean
    ) {
        viewState.updateNoteInfo(
            title = title,
            note = note,
            isPrivate = isPrivate
        )
    }

    fun findNoteInfoIfNeeded(id: Long) {
        if (isInitialized) return
        isInitialized = true
        findNoteInfo(id)
    }

    fun findNoteInfo(id: Long) {
        viewModelScope.launch {
            val info = repository.getNoteInfoById(id)
            viewState.initNoteInfo(
                EditorNoteInfoData(
                    id = info.id,
                    title = info.title,
                    time = info.timeDesc,
                    note = info.note,
                    input = info.inputText,
                    output = info.outputText,
                    isPrivate = info.isPrivate
                )
            )
        }
    }

    fun saveNoteInfo() {
        if (isSaving) return
        val info = viewState.noteInfo

        viewModelScope.launch {
            isSaving = true
            try {
                if (info.id == 0L) {
                    repository.createNoteInfo(
                        NoteInfoColumn(
                            id = 0,
                            title = info.title,
                            note = info.note,
                            timeDesc = dateFormatter.format(Date()),
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
                saveCompletedChannel.trySend(Unit)
            } finally {
                isSaving = false
            }
        }
    }
}
