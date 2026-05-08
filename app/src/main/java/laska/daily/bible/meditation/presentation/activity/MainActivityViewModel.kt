package laska.daily.bible.meditation.presentation.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import laska.daily.bible.meditation.domain.GetReadingUseCase
import laska.daily.bible.meditation.domain.Language
import laska.daily.bible.meditation.domain.audio.CheckAudioDownloadedUseCase
import laska.daily.bible.meditation.domain.audio.DeleteAllCachedAudioUseCase
import laska.daily.bible.meditation.domain.audio.DownloadAudioUseCase
import laska.daily.bible.meditation.presentation.uils.ConnectionUtils
import laska.daily.bible.meditation.presentation.uils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import laska.daily.bible.meditation.domain.settings.GetSettingsUseCase
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val isDownloadedUseCase: CheckAudioDownloadedUseCase,
    private val downloadAudioUseCase: DownloadAudioUseCase,
    private val getReadingUseCase: GetReadingUseCase,
    private val connectionUtils: ConnectionUtils,
    private val deleteAllCachedAudioUseCase: DeleteAllCachedAudioUseCase,
    private val getSettingsUseCase: GetSettingsUseCase
) : ViewModel() {

    private val _isReady = MutableStateFlow<Boolean?>(null)
    val isReady: StateFlow<Boolean?> = _isReady

    private lateinit var language: Language

    init {
        observeSettings()
        viewModelScope.launch {
            try {
                downloadActualReading()
                _isReady.value = true
            } catch (e: Exception) {
                _isReady.value = false
            }
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            getSettingsUseCase()
                .collect { settings ->
                    language = settings.language
                }
        }
    }

    suspend fun downloadActualReading() {
        try {
            delay(50)
            val actualReading = getReadingUseCase(DateUtils.todayFormatted(), language)
            val isDownloaded = isDownloadedUseCase(actualReading.audioURL)
            if (!isDownloaded) {
                deleteAllCachedAudioUseCase()
                downloadAudioUseCase(actualReading.audioURL)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun isReadyToPlay(): Boolean {
        if (!connectionUtils.isInternetAvailable()) return true
        val actualReading = getReadingUseCase(DateUtils.todayFormatted(), language)
        val isDownloaded = isDownloadedUseCase(actualReading.audioURL)
        return isDownloaded
    }
}