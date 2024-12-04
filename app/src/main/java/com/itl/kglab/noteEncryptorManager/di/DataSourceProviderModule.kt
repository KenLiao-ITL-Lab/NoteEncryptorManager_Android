package com.itl.kglab.noteEncryptorManager.di

import android.content.Context
import com.itl.kglab.noteEncryptorManager.data.PreferencesManager
import com.itl.kglab.noteEncryptorManager.data.datastore
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

}