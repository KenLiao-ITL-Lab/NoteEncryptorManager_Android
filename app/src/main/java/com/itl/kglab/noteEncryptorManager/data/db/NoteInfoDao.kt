package com.itl.kglab.noteEncryptorManager.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NoteInfoDao {

    @Insert
    fun insertInfo(info: NoteInfo)

    @Update(entity = NoteInfo::class)
    fun updateInfo(info: NoteInfo)

    @Delete
    fun deleteInfo(info: NoteInfo)

    @Query("SELECT * FROM noteInfoTable")
    fun getInfoList(list: List<NoteInfo>)

    @Query("SELECT * FROM noteInfoTable WHERE id = :id")
    fun getInfoFromList(id: Long)

}