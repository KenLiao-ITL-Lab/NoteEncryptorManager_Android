package com.itl.kglab.noteEncryptorManager.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.itl.kglab.noteEncryptorManager.tools.HashTools
import com.itl.kglab.noteEncryptorManager.ui.data.SaveNoteEventData

class MainViewModel() : ViewModel() {

    private val hashTools = HashTools()

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

}