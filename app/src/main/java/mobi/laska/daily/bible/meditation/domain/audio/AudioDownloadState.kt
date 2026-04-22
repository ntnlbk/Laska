package mobi.laska.daily.bible.meditation.domain.audio

sealed class AudioDownloadState {
    object Idle : AudioDownloadState()
    data class Downloading(val progress: Float) : AudioDownloadState()
    object Completed : AudioDownloadState()
    object Failed : AudioDownloadState()
}