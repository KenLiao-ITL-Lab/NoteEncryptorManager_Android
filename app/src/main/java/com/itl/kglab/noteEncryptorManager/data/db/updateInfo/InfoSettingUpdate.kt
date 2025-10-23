package com.itl.kglab.noteEncryptorManager.data.db.updateInfo

import androidx.room.ColumnInfo

data class InfoSettingUpdate(
    @ColumnInfo(name = "id")
    val id: Long = -1,
    @ColumnInfo(name = "sample_size")
    val sampleSize: Int = 0,
    @ColumnInfo(name = "sample_index")
    val sampleIndex: Int = 0
)