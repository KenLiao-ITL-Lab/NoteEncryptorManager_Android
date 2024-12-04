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
import com.itl.kglab.noteEncryptorManager.ui.screen.SettingScreenInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val hashTools = HashTools()

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
        return hashTools.getHashTypeList().map { it.algorithmName }
    }

    fun getSettingInfo() {
        viewModelScope.launch {
            val info = repository.getSettingInfo().stateIn(viewModelScope).value

            val screenInfo = SettingScreenInfo(
                algorithmName = info.algorithm.algorithmName,
                prefixText = info.prefixText,
                suffixText = info.suffixText,
                samplingSize = info.samplingSize,
                sampleIndex = info.sampleIndex
            )
            state = state.copy(
                settingInfo = screenInfo
            )
        }
    }

    fun saveSettingInfo(info: SettingScreenInfo) {
        viewModelScope.launch {
            repository.setSettingInfo(
                SettingInfo(
                    algorithm = HashAlgorithmType.getTypeByName(info.algorithmName),
                    prefixText = info.prefixText,
                    suffixText = info.suffixText,
                    sampleIndex = info.sampleIndex,
                    samplingSize = info.samplingSize
                )
            )

            getSettingInfo()
        }
    }

    fun clearSettingInfo() {
        getSettingInfo()
    }

}