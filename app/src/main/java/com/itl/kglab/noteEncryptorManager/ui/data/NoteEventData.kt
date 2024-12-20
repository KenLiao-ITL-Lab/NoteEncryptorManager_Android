package com.itl.kglab.noteEncryptorManager.ui.data

 data class NoteEventData(
    val inputMessage: String,
    val result: String,
    val note: String = "",
    val isPrivate: Boolean = false
)