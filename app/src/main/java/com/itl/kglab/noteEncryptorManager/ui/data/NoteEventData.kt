package com.itl.kglab.noteEncryptorManager.ui.data

 class NoteEventData(
    val inputMessage: String,
    val result: String,
    val note: String = "",
    val isPrivate: Boolean = false
) {
     companion object {
         const val ARG_INPUT = "INPUT"
         const val ARG_OUTPUT = "OUTPUT"
         const val ARG_NOTE = "NOTE"
         const val ARG_PRIVATE = "PRIVATE"
     }
 }