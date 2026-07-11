package laska.daily.bible.meditation.presentation.mainfragment

import android.content.ComponentName
import android.content.Context
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import laska.daily.bible.meditation.R
import laska.daily.bible.meditation.domain.GetReadingUseCase
import laska.daily.bible.meditation.domain.Language
import laska.daily.bible.meditation.domain.ReadingItem
import laska.daily.bible.meditation.domain.analytics.CounterType
import laska.daily.bible.meditation.domain.analytics.IncrementCounterUseCase
import laska.daily.bible.meditation.domain.audio.AudioDownloadState
import laska.daily.bible.meditation.domain.audio.CheckAudioDownloadedUseCase
import laska.daily.bible.meditation.domain.audio.DownloadAudioUseCase
import laska.daily.bible.meditation.domain.audio.ObserveDownloadAudioUseCase
import laska.daily.bible.meditation.domain.settings.GetSettingsUseCase
import laska.daily.bible.meditation.presentation.mainfragment.MainFragmentState.Companion.ERROR_INITIAL
import laska.daily.bible.meditation.presentation.mainfragment.MainFragmentState.Companion.ERROR_WHILE_CHANGING_DATES
import laska.daily.bible.meditation.presentation.service.AudioPlaybackService
import laska.daily.bible.meditation.presentation.uils.ConnectionUtils
import laska.daily.bible.meditation.presentation.uils.DateUtils
import laska.daily.bible.meditation.presentation.uils.DateUtils.Companion.todayFormatted
import javax.inject.Inject

private const val ERROR_MESSAGE = "Паспрабуйце пазней"

@HiltViewModel
class MainFragmentViewModel @OptIn(UnstableApi::class) @Inject constructor(
    private val getReadingUseCase: GetReadingUseCase,
    private val downloadAudioUseCase: DownloadAudioUseCase,
    private val observeDownloadAudioUseCase: ObserveDownloadAudioUseCase,
    private val checkAudioDownloadedUseCase: CheckAudioDownloadedUseCase,
    private val connectionUtils: ConnectionUtils,
    @param:ApplicationContext private val application: Context,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val incrementCounterUseCase: IncrementCounterUseCase
) : ViewModel() {

    private val _mainUIState = MutableStateFlow<MainFragmentState>(MainFragmentState.Progress)
    private var actualReading: ReadingItem? = null
    val mainUIState = _mainUIState.asStateFlow()

    private val _playerUIState = MutableStateFlow<AudioPlayerState>(AudioPlayerState.Initial)
    val playerUIState = _playerUIState.asStateFlow()

    private var downloadJob: Job? = null
    var currentDayIndex = 0
    lateinit var currentLanguage: Language

    private var mediaControllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    private var isFirstAudioEventSent = false
    private var isSecondAudioEventSent = false

    init {
        initializeController()
        viewModelScope.launch {
            observeSettings()
            delay(50)
            setReading()
            startProgressUpdater()
        }
    }

    @OptIn(UnstableApi::class)
    private fun initializeController() {
        val sessionToken = SessionToken(
            application,
            ComponentName(application, AudioPlaybackService::class.java)
        )
        mediaControllerFuture = MediaController.Builder(application, sessionToken).buildAsync()
        mediaControllerFuture?.addListener({
            mediaController = mediaControllerFuture?.get()
            setupPlayerListeners()
            if (_playerUIState.value is AudioPlayerState.Downloaded) {
                actualReading?.audioURL?.let { url ->
                    loadSongToPlayer(url)
                    viewModelScope.launch {
                        waitForReadyAndEmitDuration()
                    }
                }
            }

        }, ContextCompat.getMainExecutor(application)) // Выполняем в главном потоке
    }

    private fun setupPlayerListeners() {
        mediaController?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    mediaController?.seekTo(0)
                    mediaController?.pause()
                    val duration = mediaController?.duration?.coerceAtLeast(0) ?: 0
                    _playerUIState.value = AudioPlayerState.Paused(
                        formatTime(duration),
                        formatTime(0),
                        duration.toInt(),
                        0
                    )
                }
            }
        })
    }

    private fun startProgressUpdater() {
        viewModelScope.launch {
            while (true) {
                delay(200L)
                val controller = mediaController ?: continue
                val isPlaying = controller.isPlaying
                val currentPosition = controller.currentPosition.coerceAtLeast(0)
                val duration = controller.duration

                if (!isFirstAudioEventSent && currentPosition >= SECONDS_REQUIRED_FOR_FIRST_EVENT * 1000) {
                    incrementCounterUseCase(CounterType.DAILY_REFLECTION_AUDIO_PLAY)
                    isFirstAudioEventSent = true
                }

                if (!isSecondAudioEventSent && (currentPosition.toFloat() / duration.toFloat()) > (PERCENTAGE_REQUIRED_FOR_SECOND_EVENT.toFloat() / 100)) {
                    incrementCounterUseCase(CounterType.DAILY_REFLECTION_AUDIO_COMPLETED)
                    isSecondAudioEventSent = true
                }

                if (isPlaying) {
                    _playerUIState.value = AudioPlayerState.Playing(
                        currentPosition = formatTime(currentPosition),
                        progress = currentPosition.toInt(),
                        max = duration.toInt(),
                        songTime = formatTime(duration),
                    )

                } else if (duration != C.TIME_UNSET && duration > 0) {
                    val state = controller.playbackState
                    if (state == Player.STATE_READY || state == Player.STATE_ENDED) {
                        _playerUIState.value = AudioPlayerState.Paused(
                            formatTime(duration),
                            formatTime(currentPosition),
                            duration.toInt(),
                            currentPosition.toInt()
                        )
                    }
                }
            }
        }
    }


    private fun observeSettings() {
        viewModelScope.launch {
            currentLanguage = getSettingsUseCase().first().language
            getSettingsUseCase().collect { settings ->
                if (currentLanguage != settings.language) {
                    currentDayIndex = 0
                    currentLanguage = settings.language
                    setReading(language = settings.language)
                }
            }
        }
    }

    fun goForward15Sec() {
        mediaController?.let {
            var currentPosition = it.currentPosition + PLAYER_BUTTONS_CHANGE_TIME_IN_MILLS
            if (currentPosition > it.duration) currentPosition = it.duration - 1000
            it.seekTo(currentPosition)
        }
    }

    fun goBack15Sec() {
        mediaController?.let {
            val currentPosition = it.currentPosition
            it.seekTo(if (currentPosition - PLAYER_BUTTONS_CHANGE_TIME_IN_MILLS < 0L) 0L else currentPosition - PLAYER_BUTTONS_CHANGE_TIME_IN_MILLS)
        }
    }

    fun seekTo(moment: Long) {
        mediaController?.seekTo(moment)
    }

    fun pausePlayer() {
        mediaController?.pause()
    }

    fun setReading(date: String = todayFormatted(), language: Language = currentLanguage) {
        isSecondAudioEventSent = false
        isFirstAudioEventSent = false
        mediaController?.pause()
        mediaController?.clearMediaItems()
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
                if (todayFormatted() != date) {
                    _mainUIState.value = MainFragmentState.Error(ERROR_WHILE_CHANGING_DATES)
                    delay(50)
                    setReading()
                } else {
                    _mainUIState.value = MainFragmentState.Error(ERROR_INITIAL)
                }
            }
        }
    }

    fun showTextButtonClicked() {
        if (actualReading != null) {
            val duration = mediaController?.duration?.coerceAtLeast(0) ?: 0
            val position = mediaController?.currentPosition?.coerceAtLeast(0) ?: 0
            viewModelScope.launch {
                incrementCounterUseCase(CounterType.DAILY_REFLECTION_TEXT)
            }
            _mainUIState.value = MainFragmentState.TextShowed(
                DialogArguments(
                    actualReading?.bibleTextPlain ?: "",
                    bibleRef = actualReading?.bibleReference ?: "",
                    reflectionTextIntro = actualReading?.reflectionTextIntro ?: "",
                    reflectionTextBody = actualReading?.reflectionTextBody ?: "",
                    songMaxProgress = duration.toInt(),
                    actualProgress = position.toInt(),
                    date = actualReading?.dateFormatted ?: ""
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
                mediaController?.play()
            }

            AudioPlayerState.Downloading -> {}
            is AudioPlayerState.Playing -> {
                mediaController?.pause()
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
        val metadata = MediaMetadata.Builder()
            .setTitle(actualReading?.dateFormatted)
            .setArtist(application.getString(R.string.app_name))
            .build()

        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setMediaId(url)
            .setMediaMetadata(metadata)
            .build()

        mediaController?.setMediaItem(mediaItem)
        mediaController?.prepare()
        _playerUIState.value = AudioPlayerState.Downloaded
    }

    private suspend fun waitForReadyAndEmitDuration() {
        var attempts = 0
        while (mediaController?.playbackState != Player.STATE_READY && attempts < 100) {
            if (mediaController?.playerError != null) break
            delay(50)
            attempts++
        }

        val duration = mediaController?.duration?.coerceAtLeast(0) ?: 0
        val position = mediaController?.currentPosition?.coerceAtLeast(0) ?: 0
        if (duration > 0) {
            _playerUIState.value = AudioPlayerState.Paused(
                formatTime(duration),
                formatTime(position),
                duration.toInt(),
                position.toInt()
            )
            delay(100)
        }
    }

    private suspend fun getReadyItemToPlay() {
        _playerUIState.value = AudioPlayerState.Downloading
        delay(50)
        val readingUrl = actualReading?.audioURL
        if (readingUrl == null) {
            _playerUIState.value = AudioPlayerState.Error(ERROR_MESSAGE)
        } else {
            val isDownloaded = checkAudioDownloadedUseCase(readingUrl)
            if (isDownloaded) {
                loadSongToPlayer(readingUrl)
                waitForReadyAndEmitDuration()
                mediaController?.play()
            } else {
                if (connectionUtils.isInternetAvailable()) {
                    downloadAudioUseCase(readingUrl)
                    observeDownloadAudioUseCase(readingUrl).collect { domainStatus ->
                        if (domainStatus is AudioDownloadState.Completed) {
                            loadSongToPlayer(readingUrl)
                            waitForReadyAndEmitDuration()
                            mediaController?.play()
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

        if (currentDayIndex == MAX_DAY_INDEX) {
        } else if (mediaController?.isPlaying != true) {
            _mainUIState.value = MainFragmentState.Progress
            currentDayIndex += 1
            viewModelScope.launch {
                try {
                    val tomorrow =
                        DateUtils.getNextDay(actualReading?.date ?: throw Exception(ERROR_MESSAGE))
                    getReadingUseCase(
                        tomorrow,
                        actualReading?.language ?: throw Exception(ERROR_MESSAGE)
                    )
                    mediaController?.pause()
                    mediaController?.stop()
                    mediaController?.clearMediaItems()
                    setReading(tomorrow, actualReading?.language ?: throw Exception(ERROR_MESSAGE))
                } catch (e: Exception) {
                    _mainUIState.value = MainFragmentState.Error(ERROR_WHILE_CHANGING_DATES)
                    currentDayIndex -= 1
                }
            }
        }
    }

    fun goBack() {
        if (currentDayIndex == MIN_DAY_INDEX) {
        } else if (mediaController?.isPlaying != true && actualReading?.date != RELEASE_DATE_TEXT) {
            _mainUIState.value = MainFragmentState.Progress
            currentDayIndex -= 1
            viewModelScope.launch {
                try {
                    val yesterday = DateUtils.getPreviousDay(
                        actualReading?.date ?: throw Exception(ERROR_MESSAGE)
                    )
                    getReadingUseCase(
                        yesterday,
                        actualReading?.language ?: throw Exception(ERROR_MESSAGE)
                    )
                    mediaController?.pause()
                    mediaController?.stop()
                    mediaController?.clearMediaItems()
                    setReading(yesterday, actualReading?.language ?: throw Exception(ERROR_MESSAGE))
                } catch (e: Exception) {
                    currentDayIndex += 1
                    _mainUIState.value = MainFragmentState.Error(ERROR_WHILE_CHANGING_DATES)
                }
            }
        }
    }

    private fun formatTime(mills: Long): String {
        if (mills == C.TIME_UNSET) return "00:00"
        val seconds = mills / 1000
        val m = seconds / 60
        val s = seconds % 60
        if (m < 0 || s < 0) return "00:00"
        return "%02d:%02d".format(m, s)
    }

    override fun onCleared() {
        super.onCleared()
        mediaControllerFuture?.let { MediaController.releaseFuture(it) }
    }

    companion object {
        private const val PLAYER_BUTTONS_CHANGE_TIME_IN_MILLS = 15000L
        const val TOTAL_DAYS_TO_SHOW = 5
        private const val MIN_DAY_INDEX = -7
        private const val MAX_DAY_INDEX = 7
        private const val RELEASE_DATE_TEXT = "20260515"

        private const val SECONDS_REQUIRED_FOR_FIRST_EVENT = 5
        private const val PERCENTAGE_REQUIRED_FOR_SECOND_EVENT = 70
    }
}