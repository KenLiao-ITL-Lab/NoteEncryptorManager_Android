package com.itl.kglab.noteEncryptorManager.viewmodel.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itl.kglab.noteEncryptorManager.data.db.NoteInfoColumn
import com.itl.kglab.noteEncryptorManager.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: MainRepository
): ViewModel() {

    var noteInfoState by mutableStateOf(NoteInfoTableData())
        private set

    var settingState by mutableStateOf(SampleSettingData())
        private set

    fun getNoteInfoById(id: Long) {
        viewModelScope.launch {
            val info = repository.getNoteInfoById(id)
            noteInfoState = convertNoteInfoTable(info)
            settingState = convertSampleSetting(info)
        }
    }

    private fun convertNoteInfoTable(
        column: NoteInfoColumn
    ): NoteInfoTableData {
        return NoteInfoTableData(
            title = column.title,
            note = column.note,
            time = column.timeDesc,
            isPrivate = column.isPrivate
        )
    }

    private fun convertSampleSetting(
        column: NoteInfoColumn
    ): SampleSettingData {
        return SampleSettingData(
            input = column.inputText,
            output = column.outputText,
            sampleIndex = column.sampleIndex,
            sampleSize = column.sampleSize
        )
    }

}