package mobi.laska.daily.bible.meditation.presentation.mainfragment

import android.content.Context
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mobi.laska.daily.bible.meditation.domain.GetReadingUseCase
import mobi.laska.daily.bible.meditation.domain.Language
import mobi.laska.daily.bible.meditation.domain.ReadingItem
import mobi.laska.daily.bible.meditation.domain.audio.AudioDownloadState
import mobi.laska.daily.bible.meditation.domain.audio.CheckAudioDownloadedUseCase
import mobi.laska.daily.bible.meditation.domain.audio.DownloadAudioUseCase
import mobi.laska.daily.bible.meditation.domain.audio.ObserveDownloadAudioUseCase
import mobi.laska.daily.bible.meditation.presentation.uils.ConnectionUtils
import mobi.laska.daily.bible.meditation.presentation.uils.DateUtils
import mobi.laska.daily.bible.meditation.presentation.uils.DateUtils.Companion.todayFormatted
import javax.inject.Inject

private const val ERROR_MESSAGE = "Паспрабуйце пазней"

@HiltViewModel
class MainFragmentViewModel @OptIn(UnstableApi::class) @Inject constructor(
    private val getReadingUseCase: GetReadingUseCase,
    private val downloadAudioUseCase: DownloadAudioUseCase,
    private val observeDownloadAudioUseCase: ObserveDownloadAudioUseCase,
    private val checkAudioDownloadedUseCase: CheckAudioDownloadedUseCase,
    private val connectionUtils: ConnectionUtils,
    private val cacheDataSourceFactory: CacheDataSource.Factory,
    @param:ApplicationContext private val application: Context
) : ViewModel() {

    private val _mainUIState = MutableStateFlow<MainFragmentState>(MainFragmentState.Progress)
    private var actualReading: ReadingItem? = null

    val mainUIState = _mainUIState.asStateFlow()

    private val _playerUIState = MutableStateFlow<AudioPlayerState>(AudioPlayerState.Initial)
    val playerUIState = _playerUIState.asStateFlow()

    private var downloadJob: Job? = null

    private val player by lazy {
        val mediaSourceFactory =
            DefaultMediaSourceFactory(application).setDataSourceFactory(cacheDataSourceFactory)

        ExoPlayer.Builder(application).setMediaSourceFactory(mediaSourceFactory).build().apply {
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_ENDED) {
                            seekTo(0)
                            pause()
                            val duration = duration.coerceAtLeast(0)
                            _playerUIState.value = AudioPlayerState.Paused(
                                formatTime(duration),
                                formatTime(0),
                                duration.toInt(),
                                currentPosition.toInt()
                            )
                        }
                    }
                })
            }
    }

    init {
        viewModelScope.launch {
            while (true) {
                delay(200L)
                val isPlaying = player.isPlaying
                val currentPosition = player.currentPosition.coerceAtLeast(0)
                val duration = player.duration

                if (isPlaying) {
                    _playerUIState.value = AudioPlayerState.Playing(
                        currentPosition = formatTime(currentPosition),
                        progress = currentPosition.toInt()
                    )
                } else if (duration != C.TIME_UNSET && duration > 0) {
                    val state = player.playbackState
                    if (state == Player.STATE_READY || state == Player.STATE_ENDED) {
                        _playerUIState.value = AudioPlayerState.Paused(
                            formatTime(duration),
                            formatTime(player.currentPosition),
                            duration.toInt(),
                            currentPosition.toInt()
                        )
                    }
                }
            }
        }
    }

    fun goForward15Sec() {
        var currentPosition = (player.currentPosition) + PLAYER_BUTTONS_CHANGE_TIME_IN_MILLS
        if (currentPosition > player.duration) currentPosition = player.duration - 1000
        player.seekTo(
            currentPosition
        )
    }

    fun goBack15Sec() {
        val currentPosition = (player.currentPosition ?: 0)
        player.seekTo(
            if (currentPosition - PLAYER_BUTTONS_CHANGE_TIME_IN_MILLS < 0L) 0L
            else currentPosition - PLAYER_BUTTONS_CHANGE_TIME_IN_MILLS
        )
    }

    fun seekTo(moment: Long) {
        player.seekTo(moment)
    }

    fun setReading(date: String = todayFormatted(), language: Language) {
        downloadJob?.cancel()
        _mainUIState.value = MainFragmentState.Progress
        _playerUIState.value = AudioPlayerState.Initial
        viewModelScope.launch {
            try {
                actualReading = getReadingUseCase(date, language)
                _mainUIState.value = MainFragmentState.Content(
                    actualReading?.dateFormatted ?: throw Exception(ERROR_MESSAGE),
                    actualReading?.bibleReference ?: throw Exception(ERROR_MESSAGE),
                    actualReading?.feastName ?: throw Exception(ERROR_MESSAGE)
                )
                actualReading?.let {
                    val isDownloaded = checkAudioDownloadedUseCase(it.audioURL)
                    if (isDownloaded) {
                        loadSongToPlayer(it.audioURL)
                    }
                }
            } catch (e: Exception) {
                _mainUIState.value = MainFragmentState.Error(ERROR_MESSAGE)
            }
        }
    }

    fun showTextButtonClicked() {
        if (actualReading != null) {
            _mainUIState.value = MainFragmentState.TextShowed(
                DialogArguments(
                    actualReading?.bibleTextPlain ?: "",
                    bibleRef = actualReading?.bibleReference ?: "",
                    reflectionTextIntro = actualReading?.reflectionTextIntro ?: "",
                    reflectionTextBody = actualReading?.reflectionTextBody ?: "",
                    songMaxProgress = player.duration.toInt(),
                    actualProgress = player.currentPosition.toInt()
                )

            )
            _mainUIState.value = MainFragmentState.Content(
                actualReading?.dateFormatted ?: throw Exception(ERROR_MESSAGE),
                actualReading?.bibleReference ?: throw Exception(ERROR_MESSAGE),
                actualReading?.feastName ?: throw Exception(ERROR_MESSAGE)
            )
        } else {
            _mainUIState.value = MainFragmentState.Error("No text to show for now")
        }
    }

    fun playButtonClicked() {
        when (_playerUIState.value) {
            is AudioPlayerState.Downloaded, is AudioPlayerState.Paused -> {
                player.play()
            }

            AudioPlayerState.Downloading -> {}

            is AudioPlayerState.Playing -> {
                player.pause()
            }

            is AudioPlayerState.Error, AudioPlayerState.Initial -> {
                downloadJob?.cancel()
                downloadJob = viewModelScope.launch {
                    _playerUIState.value = AudioPlayerState.Downloading
                    getReadyItemToPlay()
                }
            }
        }
    }

    private fun loadSongToPlayer(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        _playerUIState.value = AudioPlayerState.Downloaded
    }

    private fun formatTime(mills: Long): String {
        if (mills == C.TIME_UNSET) return "00:00"
        val seconds = mills / 1000
        val m = seconds / 60
        val s = seconds % 60
        if (m < 0 || s < 0) return "00:00"
        return "%02d:%02d".format(m, s)
    }

    private suspend fun waitForReadyAndEmitDuration() {
        var attempts = 0
        while (player.playbackState != Player.STATE_READY && attempts < 100) {
            if (player.playerError != null) break
            delay(50)
            attempts++
        }

        val duration = player.duration.coerceAtLeast(0)
        if (duration > 0) {
            _playerUIState.value = AudioPlayerState.Paused(
                formatTime(duration),
                formatTime(player.currentPosition),
                duration.toInt(),
                player.currentPosition.toInt()
            )
            delay(100)
        }
    }

    private suspend fun getReadyItemToPlay() {
        val readingUrl = actualReading?.audioURL
        if (readingUrl == null) {
            _playerUIState.value = AudioPlayerState.Error(ERROR_MESSAGE)
        } else {
            val isDownloaded = checkAudioDownloadedUseCase(readingUrl)
            if (isDownloaded) {
                loadSongToPlayer(readingUrl)
                waitForReadyAndEmitDuration()
                player.play()
            } else {
                if (connectionUtils.isInternetAvailable()) {
                    downloadAudioUseCase(readingUrl)
                    observeDownloadAudioUseCase(readingUrl).collect { domainStatus ->
                        if (domainStatus is AudioDownloadState.Completed) {
                            loadSongToPlayer(readingUrl)
                            waitForReadyAndEmitDuration()
                            player.play()
                        } else if (domainStatus is AudioDownloadState.Failed) {
                            _playerUIState.value =
                                AudioPlayerState.Error("Failed to download audio")
                        }
                    }
                } else {
                    _playerUIState.value = AudioPlayerState.Error("No file and no connection")
                }
            }
        }
    }

    fun goForward() {
        player.pause()
        player.stop()
        player.clearMediaItems()
        val tomorrow = DateUtils.getNextDay(actualReading?.date ?: throw Exception(ERROR_MESSAGE))
        setReading(tomorrow, actualReading?.language ?: throw Exception(ERROR_MESSAGE))
    }

    fun goBack() {
        player.pause()
        player.stop()
        player.clearMediaItems()
        val yesterday =
            DateUtils.getPreviousDay(actualReading?.date ?: throw Exception(ERROR_MESSAGE))
        setReading(yesterday, actualReading?.language ?: throw Exception(ERROR_MESSAGE))
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }

    companion object {
        private const val PLAYER_BUTTONS_CHANGE_TIME_IN_MILLS = 15000L
    }
}