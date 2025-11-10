package com.itl.kglab.noteEncryptorManager.viewmodel.detail

data class NoteInfoTableData(
    val id: Long = -1,
    val title: String = "",
    val note: String = "",
    val time: String = "",
    val isPrivate: Boolean = false
)