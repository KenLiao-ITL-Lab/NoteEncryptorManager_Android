package com.itl.kglab.noteEncryptorManager.viewmodel.main

data class NoteInfoListInfoData(
    val id: Long = -1L,
    val title: String = "",
    val note: String = "",
    val time: String = "",
    val isPrivate: Boolean = false
)
