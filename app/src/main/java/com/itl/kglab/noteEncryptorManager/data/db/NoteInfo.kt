package com.itl.kglab.noteEncryptorManager.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = NoteInfo.TABLE_NAME)
data class NoteInfo(
    @PrimaryKey val id: Long,
    val title: String, // 標題
    val timeDesc: String, // 時間
    val contentText: String, // 密語
    val desc: String, // 備註
    val isPrivate: Boolean = false // 密語保護
) {
    companion object {
        const val TABLE_NAME = "noteInfoTable"
    }
}