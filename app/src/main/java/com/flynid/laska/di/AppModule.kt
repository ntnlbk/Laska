package com.flynid.laska.di

import com.flynid.laska.data.ReadingRepositoryImpl
import com.flynid.laska.domain.ReadingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserRepository(impl: ReadingRepositoryImpl): ReadingRepository {
        return impl
    }

}