package mobi.laska.daily.bible.meditation.domain.audio

import javax.inject.Inject

class ObserveDownloadAudioUseCase @Inject constructor(
    private val repository: AudioDownloadRepository
) {
    operator fun invoke(url: String) = repository.observeDownload(url)
}