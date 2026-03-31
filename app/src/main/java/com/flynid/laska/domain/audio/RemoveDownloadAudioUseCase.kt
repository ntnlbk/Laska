package com.flynid.laska.domain.audio

import javax.inject.Inject

class RemoveDownloadAudioUseCase @Inject constructor(
    private val repository: AudioDownloadRepository
) {
    operator fun invoke(url: String) = repository.removeDownloadAudio(url)
}