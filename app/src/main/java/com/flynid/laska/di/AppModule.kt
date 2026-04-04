package com.flynid.laska.di

import com.flynid.laska.data.ReadingRepositoryImpl
import com.flynid.laska.data.settings.SettingsRepositoryImpl
import com.flynid.laska.domain.ReadingRepository
import com.flynid.laska.domain.settings.SettingsRepository
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

    @Provides
    @Singleton
    fun provideSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository {
        return impl
    }

}