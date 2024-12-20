package com.itl.kglab.noteEncryptorManager.viewmodel.editor

data class EditorViewData(
    val id: Int = 0,
    val title: String = "",
    val input: String = "",
    val output: String = "",
    val note: String = "",
    val isPrivate: Boolean = false
)
