package com.flynid.laska.presentation.mainfragment

sealed class AudioPlayerState {

    data class Playing(
        val currentPosition: Int,
        val songName: String
    ) : AudioPlayerState()

    data class Downloading(
        val progress: Float
    ) : AudioPlayerState()

    data class Downloaded(
        val fileUrl: String
    ): AudioPlayerState()

    object Paused : AudioPlayerState()

    object Initial : AudioPlayerState()

    class Error(
        val message: String
    ) : AudioPlayerState()
}