package com.itl.kglab.noteEncryptorManager.di

import android.content.Context
import com.itl.kglab.noteEncryptorManager.data.db.AppDatabase
import com.itl.kglab.noteEncryptorManager.data.pref.PreferencesManager
import com.itl.kglab.noteEncryptorManager.data.pref.datastore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
        return AppDatabase.invoke(appContext)
    }

}