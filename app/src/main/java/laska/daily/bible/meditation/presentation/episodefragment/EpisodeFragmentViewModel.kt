package laska.daily.bible.meditation.presentation.episodefragment

import android.content.ComponentName
import android.content.Context
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.launch
import laska.daily.bible.meditation.R
import laska.daily.bible.meditation.domain.audio.AudioDownloadState
import laska.daily.bible.meditation.domain.audio.CheckAudioDownloadedUseCase
import laska.daily.bible.meditation.domain.audio.DownloadAudioUseCase
import laska.daily.bible.meditation.domain.audio.ObserveDownloadAudioUseCase
import laska.daily.bible.meditation.domain.courses.GetEpisodeUseCase
import laska.daily.bible.meditation.domain.courses.MarkEpisodeAsCompletedUseCase
import laska.daily.bible.meditation.presentation.service.AudioPlaybackService
import laska.daily.bible.meditation.presentation.uils.ConnectionUtils
import javax.inject.Inject

@HiltViewModel
class EpisodeFragmentViewModel @Inject constructor(
    private val getEpisodeUseCase: GetEpisodeUseCase,
    savedStateHandle: SavedStateHandle,
    private val downloadAudioUseCase: DownloadAudioUseCase,
    private val observeDownloadAudioUseCase: ObserveDownloadAudioUseCase,
    private val checkAudioDownloadedUseCase: CheckAudioDownloadedUseCase,
    @param:ApplicationContext private val application: Context,
    private val connectionUtils: ConnectionUtils,
    private val markEpisodeAsCompletedUseCase: MarkEpisodeAsCompletedUseCase
) : ViewModel() {

    private val args: EpisodeFragmentArgs =
        EpisodeFragmentArgs.fromSavedStateHandle(savedStateHandle)
    private val _episodeUIState =
        MutableStateFlow<EpisodeFragmentState>(EpisodeFragmentState.Progress)
    val episodeUIState = _episodeUIState.asStateFlow()

    private val _playerUIState = MutableStateFlow<AudioPlayerState>(
        AudioPlayerState.Initial
    )
    val playerUIState = _playerUIState.asStateFlow()

    private var downloadJob: Job? = null

    private var mediaControllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    private var isMarkedCompleted = false
    private var currentEpisodeId: Int? = null

    init {
        viewModelScope.launch {
            loadEpisodeData() // Было: setContent()
            startProgressUpdater()
        }
    }

    @OptIn(UnstableApi::class)
    private fun initializeController(url: String, title: String) {
        val sessionToken = SessionToken(
            application,
            ComponentName(
                application,
                AudioPlaybackService::class.java
            )
        )

        mediaControllerFuture =
            MediaController.Builder(application, sessionToken).buildAsync()

        mediaControllerFuture?.addListener({
            try {
                mediaController = mediaControllerFuture?.get()

                setupPlayerListeners()

                mediaController?.pause()
                mediaController?.clearMediaItems()

                _playerUIState.value = AudioPlayerState.Initial

                viewModelScope.launch {
                    val isDownloaded = checkAudioDownloadedUseCase(url)

                    if (isDownloaded) {
                        loadSongToPlayer(url, title)
                        waitForReadyAndEmitDuration()
                    }
                }
            } catch (e: Exception) {
                _playerUIState.value =
                    AudioPlayerState.Error(
                        e.message ?: "Failed to initialize audio player"
                    )
            }
        }, ContextCompat.getMainExecutor(application))
    }

    private fun startProgressUpdater() {
        viewModelScope.launch {
            while (true) {
                delay(200L)
                val controller = mediaController ?: continue
                val isPlaying = controller.isPlaying
                val currentPosition = controller.currentPosition.coerceAtLeast(0)
                val duration = controller.duration

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
                if (duration > 0 && !isMarkedCompleted && currentEpisodeId != null) {
                    val progressPercent = (currentPosition.toFloat() / duration.toFloat()) * 100f

                    if (progressPercent >= 70f) {
                        isMarkedCompleted = true
                        val episodeIdToComplete = currentEpisodeId!!
                        markEpisodeAsCompletedUseCase(episodeIdToComplete)

                    }
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

    private fun formatTime(mills: Long): String {
        if (mills == C.TIME_UNSET) return "00:00"
        val seconds = mills / 1000
        val m = seconds / 60
        val s = seconds % 60
        if (m < 0 || s < 0) return "00:00"
        return "%02d:%02d".format(m, s)
    }

    private fun loadSongToPlayer(url: String, title: String) {
        val metadata = MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(application.getString(R.string.app_name))
            .build()

        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setMediaId(url)
            .setMediaMetadata(metadata)
            .build()

        mediaController?.setMediaItem(mediaItem)
        mediaController?.prepare()
        _playerUIState.value =
            AudioPlayerState.Downloaded
    }

    fun playButtonClicked(url: String, title: String) {
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
                    getReadyItemToPlay(url, title)
                }
            }
        }
    }

    private suspend fun getReadyItemToPlay(url: String, title: String) {
        _playerUIState.value = AudioPlayerState.Downloading
        delay(50)

        val isDownloaded = checkAudioDownloadedUseCase(url)
        if (isDownloaded) {
            loadSongToPlayer(url, title)
            waitForReadyAndEmitDuration()
            mediaController?.play()
        } else {
            if (connectionUtils.isInternetAvailable()) {
                downloadAudioUseCase(url)
                observeDownloadAudioUseCase(url).collect { domainStatus ->
                    if (domainStatus is AudioDownloadState.Completed) {
                        loadSongToPlayer(url, title)
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

    private suspend fun loadEpisodeData() {
        _episodeUIState.value = EpisodeFragmentState.Progress
        try {

            mediaController?.pause()
            mediaController?.clearMediaItems()
            downloadJob?.cancel()
            _playerUIState.value = AudioPlayerState.Initial
            val episode = getEpisodeUseCase(args.courseId, args.episodeId)
            _episodeUIState.value = EpisodeFragmentState.Content(
                imageUrl = episode.imageURL,
                title = episode.title,
                reference = episode.scriptureReference,
                subtitle = episode.subtitle,
                authors = episode.authors,
                text = episode.text,
                audioUrl = episode.audioURL,
                duration = formatTime(episode.duration.toLong()),
                episodeId = episode.id
            )
            currentEpisodeId = episode.id
            initializeController(episode.audioURL, episode.title)
        } catch (e: Exception) {
            _episodeUIState.value = EpisodeFragmentState.Error(e.message.toString())
        }
    }

    companion object {
        private const val PLAYER_BUTTONS_CHANGE_TIME_IN_MILLS = 15000L
    }

}