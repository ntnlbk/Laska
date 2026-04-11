package com.flynid.laska.domain.audio

import javax.inject.Inject

class DeleteAllCachedAudioUseCase @Inject constructor(
    private val repository: AudioDownloadRepository
) {
    operator fun invoke() = repository.deleteAllCachedAudio()
}