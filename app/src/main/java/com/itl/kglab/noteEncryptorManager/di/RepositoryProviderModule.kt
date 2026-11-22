package com.itl.kglab.noteEncryptorManager.di

import com.itl.kglab.noteEncryptorManager.repository.MainRepository
import com.itl.kglab.noteEncryptorManager.repository.MainRepositoryInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryProviderModule {

    @Binds
    @Singleton
    abstract fun bindMainRepository(
        mainRepository: MainRepository
    ): MainRepositoryInterface
}
