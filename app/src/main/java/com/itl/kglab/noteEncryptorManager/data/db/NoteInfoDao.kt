package com.itl.kglab.noteEncryptorManager.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface NoteInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInfo(info: NoteInfo)

    @Update(entity = NoteInfo::class)
    fun updateInfo(info: NoteInfo)

    @Delete
    fun deleteInfo(info: NoteInfo)

    @Query("SELECT * FROM noteInfoTable")
    fun getInfoList(): List<NoteInfo>

    @Query("SELECT * FROM noteInfoTable WHERE id = :id")
    fun getInfoById(id: Long): NoteInfo

}