package com.flynid.laska.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.offline.DownloadManager
import com.flynid.laska.data.audio.AudioRepositoryImpl
import com.flynid.laska.domain.audio.AudioDownloadRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import java.util.concurrent.Executors
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DownloadManagerModule {

    @Provides
    @Singleton
    @OptIn(UnstableApi::class)
    fun provideCacheDataSourceFactory(
        cache: Cache, // Hilt injects your singleton cache here
        httpDataSourceFactory: DefaultHttpDataSource.Factory
    ): CacheDataSource.Factory {
        return CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(httpDataSourceFactory)
            .setCacheWriteDataSinkFactory(null)
    }

    @Provides
    @Singleton
    fun provideAudioDownloadRepository(impl: AudioRepositoryImpl): AudioDownloadRepository {
        return impl
    }

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideDatabaseProvider(
        @ApplicationContext context: Context
    ): StandaloneDatabaseProvider {
        return StandaloneDatabaseProvider(context)
    }

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideDownloadCache(
        @ApplicationContext context: Context,
        databaseProvider: StandaloneDatabaseProvider
    ): Cache {
        val downloadDir = File(context.cacheDir, "downloads")

        return SimpleCache(
            downloadDir,
            NoOpCacheEvictor(),
            databaseProvider
        )
    }

    @Provides
    @Singleton
    fun provideHttpDataSourceFactory(): DefaultHttpDataSource.Factory {
        return DefaultHttpDataSource.Factory()
    }

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideDownloadManager(
        @ApplicationContext context: Context,
        databaseProvider: StandaloneDatabaseProvider,
        cache: Cache,
        dataSourceFactory: DefaultHttpDataSource.Factory
    ): DownloadManager {

        val executor = Executors.newFixedThreadPool(3)

        return DownloadManager(
            context,
            databaseProvider,
            cache,
            dataSourceFactory,
            executor
        ).apply {
            maxParallelDownloads = 3
        }
    }
}