package com.flynid.laska.presentation.activity

import androidx.lifecycle.ViewModel
import com.flynid.laska.domain.GetReadingUseCase
import com.flynid.laska.domain.Language
import com.flynid.laska.domain.audio.CheckAudioDownloadedUseCase
import com.flynid.laska.domain.audio.DownloadAudioUseCase
import com.flynid.laska.presentation.uils.ConnectionUtils
import com.flynid.laska.presentation.uils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val isDownloadedUseCase: CheckAudioDownloadedUseCase,
    private val downloadAudioUseCase: DownloadAudioUseCase,
    private val getReadingUseCase: GetReadingUseCase,
    private val connectionUtils: ConnectionUtils
) : ViewModel() {

    suspend fun downloadActualReading() {
        try {
            val actualReading = getReadingUseCase(DateUtils.todayFormatted(), Language.BY)
            val isDownloaded = isDownloadedUseCase(actualReading.audioURL)
            if (!isDownloaded) {
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