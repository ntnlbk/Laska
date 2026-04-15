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
import com.flynid.laska.presentation.uils.ConnectionUtils
import com.flynid.laska.presentation.uils.DateUtils
import com.flynid.laska.presentation.uils.DateUtils.Companion.todayFormatted
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
    private val checkAudioDownloadedUseCase: CheckAudioDownloadedUseCase,
    private val connectionUtils: ConnectionUtils
) : ViewModel() {

    private val _mainUIState = MutableStateFlow<MainFragmentState>(MainFragmentState.Progress)
    private val _playerState = MutableStateFlow<AudioPlayerState>(AudioPlayerState.Initial)

    private var actualReading: ReadingItem? = null

    val mainUIState = _mainUIState.asStateFlow()
    val playerState = _playerState.asStateFlow()


    fun setReading(date: String = todayFormatted(), language: Language) {
        _mainUIState.value = MainFragmentState.Progress
        _playerState.value = AudioPlayerState.Initial
        viewModelScope.launch {
            try {
                actualReading = getReadingUseCase(date, language)
                _mainUIState.value = MainFragmentState.Content(
                    actualReading?.dateFormatted ?: throw Exception("Reading is null"),
                    actualReading?.bibleReference ?: throw Exception("Reading is null"),
                    actualReading?.feastName ?: throw Exception("Reading is null")
                )
                actualReading?.let {
                    val isDownloaded = checkAudioDownloadedUseCase(it.audioURL)
                    if (isDownloaded) {
                        _playerState.value = AudioPlayerState.Downloaded(it.audioURL)
                    }
                }
            } catch (e: Exception) {
                _mainUIState.value = MainFragmentState.Error(e.message ?: "Reading is null")
            }
        }
    }

    fun showTextButtonClicked() {
        if (actualReading != null) {
            _mainUIState.value = MainFragmentState.TextShowed(
                TextsToShow(
                    actualReading?.bibleText ?: "",
                    feastName = actualReading?.feastName ?: "",
                    reflectionTextIntro = actualReading?.reflectionTextIntro ?: "",
                    reflectionTextBody = actualReading?.reflectionTextBody ?: ""
                )

            )
            _mainUIState.value = MainFragmentState.Content(
                actualReading?.dateFormatted ?: throw Exception("Reading is null"),
                actualReading?.bibleReference ?: throw Exception("Reading is null"),
                actualReading?.feastName ?: throw Exception("Reading is null")
            )
        } else {
            _mainUIState.value = MainFragmentState.Error("No text to show for now")
        }
    }

    fun playButtonClicked() {
        when (val it = _playerState.value) {
            is AudioPlayerState.Downloaded -> {
                _playerState.value = AudioPlayerState.Playing(
                    0
                )
            }

            AudioPlayerState.Downloading -> {}
            is AudioPlayerState.Error -> {
                viewModelScope.launch {
                    _playerState.value = AudioPlayerState.Downloading
                    delay(100)
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
                _playerState.value = AudioPlayerState.Playing(it.currentPosition)
            }

            is AudioPlayerState.Playing -> {
                _playerState.value = AudioPlayerState.Paused(it.currentPosition)
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
                if (connectionUtils.isInternetAvailable()) {
                    downloadAudioUseCase(readingUrl)
                    observeDownloadAudioUseCase(readingUrl).collect { domainStatus ->
                        if (domainStatus is AudioDownloadState.Completed) {
                            _playerState.value = AudioPlayerState.Downloaded(readingUrl)
                            delay(100)
                            playButtonClicked()
                        } else if (domainStatus is AudioDownloadState.Failed) {
                            _playerState.value =
                                AudioPlayerState.Error("Failed to download audio")
                        }
                    }
                } else {
                    _playerState.value = AudioPlayerState.Error("No file and no connection")
                }
            }
        }
    }

    fun goForward() {
        val tomorrow =
            DateUtils.getNextDay(actualReading?.date ?: throw Exception("Reading is null"))
        setReading(tomorrow, actualReading?.language ?: throw Exception("Reading is null"))
    }

    fun goBack() {
        val yesterday =
            DateUtils.getPreviousDay(actualReading?.date ?: throw Exception("Reading is null"))
        setReading(yesterday, actualReading?.language ?: throw Exception("Reading is null"))
    }

}