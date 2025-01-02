package com.itl.kglab.noteEncryptorManager.viewmodel.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itl.kglab.noteEncryptorManager.repository.MainRepository
import com.itl.kglab.noteEncryptorManager.ui.screen.detail.DetailViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: MainRepository
): ViewModel() {

    var viewState = DetailViewState()
        private set

    fun getNoteInfoById(id: Long) {
        viewModelScope.launch {
            val info = repository.getNoteInfoById(id)
            viewState.updateNoteInfo(info)
        }
    }

}