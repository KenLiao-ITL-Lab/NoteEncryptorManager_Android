package com.itl.kglab.noteEncryptorManager.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = NoteInfo.TABLE_NAME)
data class NoteInfo(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = -1,
    @ColumnInfo(name = "title")
    val title: String = "", // 標題
    @ColumnInfo(name = "time_desc")
    val timeDesc: String = "", // 時間
    @ColumnInfo(name = "input_text")
    val inputText: String = "", // 密語
    @ColumnInfo(name = "output_text")
    val outputText: String = "", // 輸入 - 要點開才可查看
    @ColumnInfo(name = "note")
    val note: String = "", // 備註
    @ColumnInfo(name = "is_private")
    val isPrivate: Boolean = false, // 密語保護
    @ColumnInfo(name = "sample_size")
    val sampleSize: Int = 0,
    @ColumnInfo(name = "sample_index")
    val sampleIndex: Int = 0
) {
    companion object {
        const val TABLE_NAME = "noteInfoTable"
    }
}