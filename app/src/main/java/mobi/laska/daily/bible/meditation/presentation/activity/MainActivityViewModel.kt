package mobi.laska.daily.bible.meditation.presentation.activity

import androidx.lifecycle.ViewModel
import mobi.laska.daily.bible.meditation.domain.GetReadingUseCase
import mobi.laska.daily.bible.meditation.domain.Language
import mobi.laska.daily.bible.meditation.domain.audio.CheckAudioDownloadedUseCase
import mobi.laska.daily.bible.meditation.domain.audio.DeleteAllCachedAudioUseCase
import mobi.laska.daily.bible.meditation.domain.audio.DownloadAudioUseCase
import mobi.laska.daily.bible.meditation.presentation.uils.ConnectionUtils
import mobi.laska.daily.bible.meditation.presentation.uils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val isDownloadedUseCase: CheckAudioDownloadedUseCase,
    private val downloadAudioUseCase: DownloadAudioUseCase,
    private val getReadingUseCase: GetReadingUseCase,
    private val connectionUtils: ConnectionUtils,
    private val deleteAllCachedAudioUseCase: DeleteAllCachedAudioUseCase
) : ViewModel() {

    suspend fun downloadActualReading() {
        try {
            val actualReading = getReadingUseCase(DateUtils.todayFormatted(), Language.BY)
            val isDownloaded = isDownloadedUseCase(actualReading.audioURL)
            if (!isDownloaded) {
                // usecase: если мы предзагружаем актуальную дату, удалить весь кеш.
                deleteAllCachedAudioUseCase()
                downloadAudioUseCase(actualReading.audioURL)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun isReadyToPlay(): Boolean {
        if (!connectionUtils.isInternetAvailable()) return true
        val actualReading = getReadingUseCase(DateUtils.todayFormatted(), Language.BY)
        val isDownloaded = isDownloadedUseCase(actualReading.audioURL)
        return isDownloaded
    }
}