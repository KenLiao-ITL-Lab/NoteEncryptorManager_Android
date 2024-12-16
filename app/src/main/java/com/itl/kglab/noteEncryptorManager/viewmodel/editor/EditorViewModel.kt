package com.itl.kglab.noteEncryptorManager.viewmodel.editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor () : ViewModel() {

    private var viewData = EditorViewData()

    private val _viewStateLiveData: MutableLiveData<EditorViewState> = MutableLiveData()
    val viewStateLiveData: LiveData<EditorViewState> get() = _viewStateLiveData!!

    fun setViewData(
        data: EditorViewData
    ) {
        viewData = data
    }

}