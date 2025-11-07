package com.itl.kglab.noteEncryptorManager.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.itl.kglab.noteEncryptorManager.data.db.updateInfo.InfoSettingUpdate

@Dao
interface NoteInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInfo(info: NoteInfoColumn)

    @Update(entity = NoteInfoColumn::class)
    fun updateInfo(info: NoteInfoColumn)

    @Delete
    fun deleteInfo(info: NoteInfoColumn)

    @Query("SELECT * FROM noteInfoTable")
    fun getInfoList(): List<NoteInfoColumn>

    @Query("SELECT * FROM noteInfoTable WHERE id = :id")
    fun getInfoById(id: Long): NoteInfoColumn

    @Update(
        onConflict = OnConflictStrategy.REPLACE,
        entity = NoteInfoColumn::class
    )
    fun updateInfoSetting(data: InfoSettingUpdate)

}