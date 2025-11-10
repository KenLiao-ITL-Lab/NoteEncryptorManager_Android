package com.itl.kglab.noteEncryptorManager.viewmodel.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itl.kglab.noteEncryptorManager.data.db.NoteInfoColumn
import com.itl.kglab.noteEncryptorManager.repository.MainRepository
import com.itl.kglab.noteEncryptorManager.tools.HashTools
import com.itl.kglab.noteEncryptorManager.tools.SettingInputRegex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: MainRepository,
): ViewModel() {

    var noteInfoState by mutableStateOf(NoteInfoTableData())
        private set

    var settingState by mutableStateOf(SampleSettingData())
        private set

    fun setInput(input: String) {
        settingState = settingState.copy(
            input = input
        )
    }

    fun setSampleIndex(indexString: String) {
        settingState = settingState.copy(
            sampleIndex = indexString
        )
    }

    fun setSampleSize(sizeString: String) {
        settingState = settingState.copy(
            sampleSize = sizeString
        )
    }

    fun getNoteInfoById(id: Long) {
        viewModelScope.launch {
            val info = repository.getNoteInfoById(id)
            noteInfoState = convertNoteInfoTable(info)
            settingState = convertSampleSetting(info)
        }
    }

    fun convertMessage() {
        viewModelScope.launch {
            val tool = getHashTool()
            val hashMessage = tool.hashMessage(settingState.input)
            settingState = settingState.copy(
                output = hashMessage
            )
        }
    }

    fun sampleMessage() {
        if (settingState.output.isEmpty()) return
        viewModelScope.launch {
            val tool = getHashTool()
            val index = checkIndexString(settingState.sampleIndex)
            val size = checkSizeString(settingState.sampleSize)
            val sampledMessage = tool.sampleMessage(
                message = settingState.output,
                index = index,
                size =  size
            )
            settingState = settingState.copy(
                sampleIndex = index.toString(),
                sampleSize = size.toString(),
                sampledMessage = sampledMessage
            )
        }
    }

    private fun checkIndexString(indexString: String): Int {
        return if (indexString.matches(SettingInputRegex.sampleIndexCheckRegex))
            indexString.toInt()
        else
            SettingInputRegex.DEFAULT_INDEX
    }

    private fun checkSizeString(sizeString: String): Int {
        return if (sizeString.matches(SettingInputRegex.sampleSizeCheckRegex))
            sizeString.toInt()
        else
            SettingInputRegex.DEFAULT_SIZE
    }

    private suspend fun getHashTool(): HashTools {
        val info = repository.getSettingInfo().stateIn(viewModelScope).value
        return HashTools(info)
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
            sampleIndex = column.sampleIndex.toString(),
            sampleSize = column.sampleSize.toString()
        )
    }

}