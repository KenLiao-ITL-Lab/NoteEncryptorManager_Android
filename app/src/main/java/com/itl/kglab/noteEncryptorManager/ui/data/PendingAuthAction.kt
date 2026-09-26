package com.itl.kglab.noteEncryptorManager.ui.data

sealed interface PendingAuthAction {
    data class OpenDetail(val id: Long) : PendingAuthAction
    data class EditNote(val id: Long) : PendingAuthAction
    data class DeleteNote(val id: Long) : PendingAuthAction
    data class ToggleBiometric(val targetState: Boolean) : PendingAuthAction
}
