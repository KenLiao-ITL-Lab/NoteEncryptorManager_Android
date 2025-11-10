package com.itl.kglab.noteEncryptorManager.data.db.updateInfo

import androidx.room.ColumnInfo

data class NoteInfoUpdate(
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "title")
    val title: String = "", // 標題
    @ColumnInfo(name = "time_desc")
    val timeDesc: String = "", // 時間
    @ColumnInfo(name = "note")
    val note: String = "", // 備註
    @ColumnInfo(name = "is_private")
    val isPrivate: Boolean = false, // 密語保護
)
