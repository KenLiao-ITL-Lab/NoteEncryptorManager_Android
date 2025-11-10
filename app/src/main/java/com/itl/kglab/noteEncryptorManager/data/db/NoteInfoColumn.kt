package com.itl.kglab.noteEncryptorManager.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = NoteInfoColumn.TABLE_NAME)
data class NoteInfoColumn(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = -1,
    @ColumnInfo(name = "title", defaultValue = "Unname Title")
    val title: String = "Unname Title", // 標題
    @ColumnInfo(name = "time_desc", defaultValue = "Undefine")
    val timeDesc: String = "", // 時間
    @ColumnInfo(name = "note", defaultValue = "")
    val note: String = "", // 備註
    @ColumnInfo(name = "is_private", defaultValue = "0")
    val isPrivate: Boolean = false, // 密語保護
    @ColumnInfo(name = "input_text", defaultValue = "")
    val inputText: String = "", // 訊息原文
    @ColumnInfo(name = "output_text", defaultValue = "")
    val outputText: String = "", // 訊息密文
    @ColumnInfo(name = "sampled_message", defaultValue = "")
    val sampledMessage: String = "",
    @ColumnInfo(name = "sample_size", defaultValue = "0")
    val sampleSize: Int = 0,
    @ColumnInfo(name = "sample_index", defaultValue = "0")
    val sampleIndex: Int = 0,
    @ColumnInfo(name = "prefix", defaultValue = "")
    val prefix: String = "",
    @ColumnInfo(name = "suffix", defaultValue = "")
    val suffix: String = ""
) {
    companion object {
        const val TABLE_NAME = "noteInfoTable"
    }
}