package com.itl.kglab.noteEncryptorManager.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [
        NoteInfo::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

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