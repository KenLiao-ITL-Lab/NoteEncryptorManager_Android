package com.itl.kglab.noteEncryptorManager.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itl.kglab.noteEncryptorManager.repository.MainRepository
import com.itl.kglab.noteEncryptorManager.tools.HashAlgorithmType
import com.itl.kglab.noteEncryptorManager.tools.HashTools
import com.itl.kglab.noteEncryptorManager.tools.SettingInfo
import com.itl.kglab.noteEncryptorManager.ui.data.SaveNoteEventData
import com.itl.kglab.noteEncryptorManager.ui.screen.data.SettingScreenInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private lateinit var hashTools: HashTools

    var state by mutableStateOf(ScreenState())

    var resultState by mutableStateOf("")
        private set

    init {
        getSettingInfo()
    }

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
        return HashTools.getHashTypeList().map { it.algorithmName }
    }

    fun getSettingInfo() {
        viewModelScope.launch {
            val info = repository.getSettingInfo().stateIn(viewModelScope).value

            val screenInfo = SettingScreenInfo(
                algorithmIndex = getAlgorithmListIndexByName(info.algorithmName),
                info = info,
            )
            state = state.copy(
                settingInfo = screenInfo
            )

            hashTools = HashTools(state.settingInfo.info)
        }
    }

    private fun getAlgorithmListIndexByName(algorithmName: String): Int {
        val index = getHashTypeList().indexOf(algorithmName)
        return if (index == -1) 0 else index
    }

    fun saveSettingInfo(screenInfo: SettingScreenInfo) {
        viewModelScope.launch {
            repository.setSettingInfo(screenInfo.info)

            getSettingInfo()
        }
    }

    fun clearSettingInfo() {
        getSettingInfo()
    }

}