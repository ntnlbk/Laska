package com.flynid.laska.domain.audio

import javax.inject.Inject

class CheckAudioDownloadedUseCase @Inject constructor(
    private val repository: AudioDownloadRepository
) {
    suspend operator fun invoke(url: String): Boolean {
        return repository.isAudioDownloaded(url)
    }
}