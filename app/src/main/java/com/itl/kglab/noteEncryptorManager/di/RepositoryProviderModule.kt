package com.itl.kglab.noteEncryptorManager.di

import com.itl.kglab.noteEncryptorManager.data.db.AppDatabase
import com.itl.kglab.noteEncryptorManager.data.pref.PreferencesManager
import com.itl.kglab.noteEncryptorManager.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
class RepositoryProviderModule {

    @Provides
    fun provideMainRepository(
        preferencesManager: PreferencesManager,
        database: AppDatabase,
        ioDispatcher: CoroutineDispatcher
    ): MainRepository {
        return MainRepository(
            preferencesManager = preferencesManager,
            database = database,
            ioDispatcher = ioDispatcher
        )
    }

}