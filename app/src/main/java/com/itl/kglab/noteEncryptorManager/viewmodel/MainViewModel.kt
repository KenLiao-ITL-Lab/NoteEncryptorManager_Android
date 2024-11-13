package com.itl.kglab.noteEncryptorManager.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    var resultState by mutableStateOf("")
        private set

    fun convertInput(input: String) {
        resultState = "Result message -> $input"
    }

    fun duplicateResult() {
        // 複製結果
    }

    fun saveResult() {
        // 儲存結果
    }

    fun clear() {
        resultState = ""
    }
}