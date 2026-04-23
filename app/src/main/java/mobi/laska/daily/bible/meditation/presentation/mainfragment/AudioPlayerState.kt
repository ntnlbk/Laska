package mobi.laska.daily.bible.meditation.presentation.mainfragment

sealed class AudioPlayerState {

    data class Playing(
        val currentPosition: String,
        val progress: Int
    ) : AudioPlayerState()

    object Downloading : AudioPlayerState()

   object Downloaded: AudioPlayerState()

    data class Paused(
        val songTime: String,
        val currentPosition: String,
        val maxProgress: Int,
        val progress: Int
    ): AudioPlayerState(){

    }

    object Initial : AudioPlayerState()

    data class Error(
        val message: String
    ) : AudioPlayerState()
}