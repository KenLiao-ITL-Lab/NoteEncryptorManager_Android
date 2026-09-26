package com.itl.kglab.noteEncryptorManager.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        NoteInfoColumn::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun noteInfoDao(): NoteInfoDao

}