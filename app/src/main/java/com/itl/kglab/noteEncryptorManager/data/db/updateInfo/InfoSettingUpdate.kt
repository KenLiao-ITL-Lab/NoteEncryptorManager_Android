package com.itl.kglab.noteEncryptorManager.data.db.updateInfo

import androidx.room.ColumnInfo

data class InfoSettingUpdate(
//    @ColumnInfo(name = "id")
    val id: Long = -1,
    @ColumnInfo(name = "input_text")
    val inputText: String = "", // 訊息原文
    @ColumnInfo(name = "output_text")
    val outputText: String = "", // 訊息密文
    @ColumnInfo(name = "sampled_message")
    val sampledMessage: String = "", // 取樣密文
    @ColumnInfo(name = "sample_size")
    val sampleSize: Int = 0,
    @ColumnInfo(name = "sample_index")
    val sampleIndex: Int = 0,
)