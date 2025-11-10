package com.itl.kglab.noteEncryptorManager.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        NoteInfoColumn::class
    ],
    version = 2,
    exportSchema = true,
    autoMigrations = []
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun noteInfoDao(): NoteInfoDao

    companion object {
        @Volatile
        private var db: AppDatabase? = null

        private val LOCK = Any()

        private fun build(context: Context) =
            Room.databaseBuilder(
                context = context,
                klass = AppDatabase::class.java,
                name = "NoteInfoData"
            )
                .build()

        operator fun invoke(context: Context) = db ?: synchronized(LOCK) {
            db ?: build(context).also { db = it }
        }
    }
}