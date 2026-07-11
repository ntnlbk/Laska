package laska.daily.bible.meditation.di

import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import laska.daily.bible.meditation.data.ReadingRepositoryImpl
import laska.daily.bible.meditation.data.settings.SettingsRepositoryImpl
import laska.daily.bible.meditation.domain.ReadingRepository
import laska.daily.bible.meditation.domain.settings.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import laska.daily.bible.meditation.data.firebase.AnalyticsRepositoryImpl
import laska.daily.bible.meditation.domain.analytics.AnalyticsRepository
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

    @Provides
    @Singleton
    fun provideAnalyticsRepository(impl: AnalyticsRepositoryImpl): AnalyticsRepository{
        return impl
    }

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(): FirebaseAnalytics{
        return Firebase.analytics
    }

}