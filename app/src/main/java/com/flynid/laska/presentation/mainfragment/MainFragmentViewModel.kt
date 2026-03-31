package com.flynid.laska.presentation.mainfragment

import android.content.Context
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.flynid.laska.data.audio.DownloadRepository
import com.flynid.laska.domain.GetReadingUseCase
import com.flynid.laska.domain.Language
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @OptIn(UnstableApi::class)
@Inject constructor(
    private val getReadingUseCase: GetReadingUseCase,
    private val repository: DownloadRepository,
    private val cache: Cache,
    private val httpDataSourceFactory: DefaultHttpDataSource.Factory,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow<MainFragmentState>(MainFragmentState.Progress)

    val state = _state.asStateFlow()

    fun showReadingText(date: String, language: Language) {

        _state.value = MainFragmentState.Progress

        viewModelScope.launch {
            val item = getReadingUseCase(date, language)
            _state.value =
                MainFragmentState.Content(item.reflectionTextBody)
        }

    }

    @OptIn(UnstableApi::class)
    suspend fun play(date: String, language: Language) {
        val item = getReadingUseCase(date, language)
        Log.d("MY_TEST", item.audioURL)
        val url = item.audioURL
        repository.downloadMp3(
            url
        )
        val cacheDataSourceFactory: DataSource.Factory =
            CacheDataSource.Factory()
                .setCache(cache)
                .setUpstreamDataSourceFactory(httpDataSourceFactory)
                .setCacheWriteDataSinkFactory(null) // Disable writing.

        val player =
            ExoPlayer.Builder(context)
                .setMediaSourceFactory(
                    DefaultMediaSourceFactory(context).setDataSourceFactory(
                        cacheDataSourceFactory
                    )
                )
                .build()


        player.setMediaItem(MediaItem.fromUri(url))
        player.prepare()
        player.play()
    }

}