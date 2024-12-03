package com.itl.kglab.noteEncryptorManager.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itl.kglab.noteEncryptorManager.repository.MainRepository
import com.itl.kglab.noteEncryptorManager.tools.HashTools
import com.itl.kglab.noteEncryptorManager.ui.data.SaveNoteEventData
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    val repository: MainRepository
) : ViewModel() {

    private val hashTools = HashTools()

    var state by mutableStateOf(ScreenState())

    var resultState by mutableStateOf("")
        private set

    fun convertInput(input: String) {
        resultState = hashTools.hashMessage(input)
    }

    fun saveResult(data: SaveNoteEventData) {
        // 儲存結果
    }

    fun clear() {
        resultState = ""
    }

    fun getHashTypeList(): List<String> {
        return hashTools.getHashTypeList().map { it.algorithmName }
    }

    fun getSettingInfo() {
        viewModelScope.launch {
            val info = repository.getSettingInfo().stateIn(viewModelScope).value
            state = state.copy(
                settingInfo = info
            )
        }
    }

}