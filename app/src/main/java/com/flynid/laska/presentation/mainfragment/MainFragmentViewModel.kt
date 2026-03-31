package com.flynid.laska.presentation.mainfragment

import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.flynid.laska.domain.GetReadingUseCase
import com.flynid.laska.domain.Language
import com.flynid.laska.domain.audio.AudioDownloadState
import com.flynid.laska.domain.audio.CheckAudioDownloadedUseCase
import com.flynid.laska.domain.audio.DownloadAudioUseCase
import com.flynid.laska.domain.audio.ObserveDownloadAudioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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

    val mainUIState = _mainUIState.asStateFlow()
    val playerState = _playerState.asStateFlow()

    fun showReadingText(date: String, language: Language) {

        _mainUIState.value = MainFragmentState.Progress

        viewModelScope.launch {
            val item = getReadingUseCase(date, language)
            _mainUIState.value = MainFragmentState.Content(item.reflectionTextBody)
        }

    }

    fun updatePlayerState(
        isPlaying: Boolean
    ) {
        _playerState.value =
            if (isPlaying)
                AudioPlayerState.Playing(0, "")
            else
                AudioPlayerState.Paused
    }

    @OptIn(UnstableApi::class)
    fun play(date: String, language: Language) {
        viewModelScope.launch {
            try {
                // 1. Get the URL
                val item = getReadingUseCase(date, language)
                val url = item.audioURL

                // 2. CHECK IF ALREADY DOWNLOADED IN CACHE
                val isDownloaded = checkAudioDownloadedUseCase(url)
                if (isDownloaded) {
                    // It's already fully downloaded! Jump straight to playing offline!
                    _playerState.value = AudioPlayerState.Downloaded(url)
                    return@launch // Stop here, do not start a new download!
                }

                // 3. IF NOT DOWNLOADED, START DOWNLOAD AND OBSERVE
                downloadAudioUseCase(url)

                observeDownloadAudioUseCase(url).collect { domainStatus ->
                    _playerState.value = when (domainStatus) {
                        is AudioDownloadState.Idle -> AudioPlayerState.Initial
                        is AudioDownloadState.Downloading -> AudioPlayerState.Downloading(domainStatus.progress)
                        is AudioDownloadState.Completed -> AudioPlayerState.Downloaded(url)
                        is AudioDownloadState.Failed -> AudioPlayerState.Error("Failed to download audio")
                    }
                }
            } catch (e: Exception) {
                // If the user is offline and `getReadingUseCase` fails
                _playerState.value = AudioPlayerState.Error("Network error: Could not fetch reading")
            }
        }
    }

}