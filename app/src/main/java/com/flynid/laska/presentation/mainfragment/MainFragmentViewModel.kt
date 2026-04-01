package com.flynid.laska.presentation.mainfragment

import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.flynid.laska.domain.GetReadingUseCase
import com.flynid.laska.domain.Language
import com.flynid.laska.domain.ReadingItem
import com.flynid.laska.domain.audio.AudioDownloadState
import com.flynid.laska.domain.audio.CheckAudioDownloadedUseCase
import com.flynid.laska.domain.audio.DownloadAudioUseCase
import com.flynid.laska.domain.audio.ObserveDownloadAudioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @OptIn(UnstableApi::class) @Inject constructor(
    private val getReadingUseCase: GetReadingUseCase,
    private val downloadAudioUseCase: DownloadAudioUseCase,
    private val observeDownloadAudioUseCase: ObserveDownloadAudioUseCase,
    private val checkAudioDownloadedUseCase: CheckAudioDownloadedUseCase
) : ViewModel() {

    private val _mainUIState = MutableStateFlow<MainFragmentState>(MainFragmentState.Progress)
    private val _playerState = MutableStateFlow<AudioPlayerState>(AudioPlayerState.Initial)

    private var actualReading: ReadingItem? = null

    val mainUIState = _mainUIState.asStateFlow()
    val playerState = _playerState.asStateFlow()


    fun setReading(date: String, language: Language) {
        _mainUIState.value = MainFragmentState.Progress
        viewModelScope.launch {
            try {
                actualReading = getReadingUseCase(date, language)
                _mainUIState.value = MainFragmentState.Content(
                    actualReading?.bibleText ?: throw Exception("Reading is null")
                )
            } catch (e: Exception) {
                _mainUIState.value = MainFragmentState.Error(e.message ?: "Reading is null")
            }
        }
    }

    fun playButtonClicked() {
        when (_playerState.value) {
            is AudioPlayerState.Downloaded -> {
                _playerState.value = AudioPlayerState.Playing(
                    0
                )
            }
            AudioPlayerState.Downloading -> {}
            is AudioPlayerState.Error -> {
                viewModelScope.launch {
                    _playerState.value = AudioPlayerState.Downloading
                    getReadyItemToPlay()
                }
            }

            AudioPlayerState.Initial -> {
                viewModelScope.launch {
                    _playerState.value = AudioPlayerState.Downloading
                    getReadyItemToPlay()
                }
            }

            is AudioPlayerState.Paused -> {
                _playerState.value = AudioPlayerState.Playing(0)
            }
            is AudioPlayerState.Playing -> {
                _playerState.value = AudioPlayerState.Paused(0)
            }
        }
    }

    private suspend fun getReadyItemToPlay() {
        val readingUrl = actualReading?.audioURL
        if (readingUrl == null) {
            _playerState.value = AudioPlayerState.Error("Reading is null")
        } else {
            val isDownloaded = checkAudioDownloadedUseCase(readingUrl)
            if (isDownloaded) {
                _playerState.value = AudioPlayerState.Downloaded(readingUrl)
                delay(100)
                playButtonClicked()
            } else {
                downloadAudioUseCase(readingUrl)
                observeDownloadAudioUseCase(readingUrl).collect { domainStatus ->
                    if (domainStatus is AudioDownloadState.Completed) {
                        _playerState.value = AudioPlayerState.Downloaded(readingUrl)
                        delay(100)
                        playButtonClicked()
                    } else if (domainStatus is AudioDownloadState.Failed) {
                        _playerState.value = AudioPlayerState.Error("Failed to download audio")
                    }
                }
            }
        }
    }
}