package com.itl.kglab.noteEncryptorManager.viewmodel.detail

import androidx.lifecycle.ViewModel
import com.itl.kglab.noteEncryptorManager.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    val repository: MainRepository
): ViewModel() {



}