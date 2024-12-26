package com.itl.kglab.noteEncryptorManager.ui.data

 data class NoteEventData(
    val title: String = "",
    val inputMessage: String,
    val outputMessage: String,
    val note: String = "",
    val isPrivate: Boolean = false
)