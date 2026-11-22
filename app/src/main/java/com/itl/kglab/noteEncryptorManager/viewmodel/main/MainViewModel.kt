package com.itl.kglab.noteEncryptorManager.viewmodel.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itl.kglab.noteEncryptorManager.repository.MainRepositoryInterface
import com.itl.kglab.noteEncryptorManager.tools.HashTools
import com.itl.kglab.noteEncryptorManager.tools.SettingInfo
import com.itl.kglab.noteEncryptorManager.ui.data.PendingAuthAction
import com.itl.kglab.noteEncryptorManager.ui.screen.main.data.SettingScreenInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepositoryInterface
) : ViewModel() {

    private var hashTools: HashTools = HashTools(SettingInfo())

    var state by mutableStateOf(ScreenState())
        private set

    var inputState by mutableStateOf("")
        private set

    var resultState by mutableStateOf("")
        private set

    var pendingAuthAction by mutableStateOf<PendingAuthAction?>(null)
        private set

    init {
        getSettingInfo()
        getNoteInfoList()
    }

    fun updateInput(input: String) {
        inputState = input
    }

    fun convertInput(input: String) {
        resultState = hashTools.hashMessage(input)
    }

    fun clear() {
        inputState = ""
        resultState = ""
    }

    fun updatePendingAuthAction(action: PendingAuthAction?) {
        pendingAuthAction = action
    }

    fun clearPendingAuthAction() {
        pendingAuthAction = null
    }

    fun updateSystemBiometricReady(isReady: Boolean) {
        if (state.isSystemBiometricReady != isReady) {
            state = state.copy(isSystemBiometricReady = isReady)
        }
    }

    fun disableBiometricBecauseSystemRemoved() {
        clearPendingAuthAction()
        val currentSetting = state.settingInfo
        val updatedSetting = currentSetting.copy(
            isBiometricEnabled = false,
            info = currentSetting.info.copy(isBiometricEnabled = false)
        )
        state = state.copy(
            settingInfo = updatedSetting,
            isSystemBiometricReady = false
        )
        viewModelScope.launch {
            repository.setSettingInfo(updatedSetting.info)
        }
    }

    fun checkPrivateNoteCount(onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val count = repository.getPrivateNoteCount()
            onResult(count)
        }
    }

    fun getHashTypeList(): List<String> {
        return HashTools.getHashTypeList().map { it.algorithmName }
    }

    private fun getSettingInfo() {
        viewModelScope.launch {
            val info = repository.getSettingInfo().first()

            val screenInfo = SettingScreenInfo(
                algorithmIndex = getAlgorithmListIndexByName(info.algorithmName),
                isBiometricEnabled = info.isBiometricEnabled,
                info = info,
            )
            state = state.copy(
                settingInfo = screenInfo,
                isSettingLoaded = true
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

    fun getNoteInfoList() {
        viewModelScope.launch {
            val list = repository.getNoteList()
            state = state.copy(
                noteInfoList = list.map { source ->
                    NoteInfoListInfoData(
                        id = source.id,
                        title = source.title,
                        note = source.note,
                        time = source.timeDesc,
                        isPrivate = source.isPrivate
                    )
                }
            )
        }
    }

    fun deleteNoteInfoById(id: Long) {
        viewModelScope.launch {
            repository.deleteNoteById(id)
            getNoteInfoList()
        }
    }
}
