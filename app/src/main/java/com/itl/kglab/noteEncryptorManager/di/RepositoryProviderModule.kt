package com.itl.kglab.noteEncryptorManager.di

import com.itl.kglab.noteEncryptorManager.data.PreferencesManager
import com.itl.kglab.noteEncryptorManager.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
class RepositoryProviderModule {

    @Provides
    fun provideMainRepository(
        preferencesManager: PreferencesManager
    ): MainRepository {
        return MainRepository(
            preferencesManager = preferencesManager
        )
    }

}