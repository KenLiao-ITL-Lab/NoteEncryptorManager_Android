package com.itl.kglab.noteEncryptorManager.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = NoteInfo.TABLE_NAME)
data class NoteInfo(
    @PrimaryKey val id: Long,
    val title: String,
    val timeDesc: String,
    val note: String,
    val isPrivate: Boolean = false
) {
    companion object {
        const val TABLE_NAME = "noteInfoTable"
    }
}