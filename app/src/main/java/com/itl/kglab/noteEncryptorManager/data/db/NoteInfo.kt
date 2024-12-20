package com.itl.kglab.noteEncryptorManager.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = NoteInfo.TABLE_NAME)
data class NoteInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String, // 標題
    val timeDesc: String, // 時間
    val inputText: String, // 密語
    val outputText: String, // 輸入 - 要點開才可查看
    val note: String, // 備註 - 要點開才可以查看
    val isPrivate: Boolean = false // 密語保護
) {
    companion object {
        const val TABLE_NAME = "noteInfoTable"
    }
}