package mobi.laska.daily.bible.meditation.presentation.mainfragment

sealed class AudioPlayerState {

    data class Playing(
        val currentPosition: Int
    ) : AudioPlayerState()

    object Downloading : AudioPlayerState()

    data class Downloaded(
        val fileUrl: String
    ): AudioPlayerState()

    data class Paused(
        val currentPosition: Int
    ) : AudioPlayerState()

    object Initial : AudioPlayerState()

    data class Error(
        val message: String
    ) : AudioPlayerState()
}