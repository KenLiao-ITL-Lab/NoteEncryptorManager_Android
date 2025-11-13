package com.itl.kglab.noteEncryptorManager.viewmodel.editor

data class EditorNoteInfoData(
    val id: Long = -1,
    val title: String = "",
    val time: String = "",
    val note: String = "",
    val input: String = "",
    val output: String = "",
    val isPrivate: Boolean = false
)
