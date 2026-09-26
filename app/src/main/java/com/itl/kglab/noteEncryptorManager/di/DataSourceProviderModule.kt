package com.itl.kglab.noteEncryptorManager.di

import android.content.Context
import androidx.room.Room
import com.itl.kglab.noteEncryptorManager.data.db.AppDatabase
import com.itl.kglab.noteEncryptorManager.data.db.NoteInfoDao
import com.itl.kglab.noteEncryptorManager.data.pref.PreferencesManager
import com.itl.kglab.noteEncryptorManager.data.pref.datastore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceProviderModule {

    @Singleton
    @Provides
    fun providerDataStoreManager(
        @ApplicationContext appContext: Context
    ): PreferencesManager {
        return PreferencesManager(
            dataStore = appContext.datastore
        )
    }

    @Singleton
    @Provides
    fun providerDatabase(
        @ApplicationContext appContext: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context = appContext,
            klass = AppDatabase::class.java,
            name = "NoteInfoData"
        ).build()
    }

    @Provides
    fun provideNoteInfoDao(database: AppDatabase): NoteInfoDao {
        return database.noteInfoDao()
    }

    @Provides
    fun provideIoDispatchers(): CoroutineDispatcher = Dispatchers.IO

}