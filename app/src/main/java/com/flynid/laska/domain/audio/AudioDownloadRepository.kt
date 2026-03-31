package com.flynid.laska.domain.audio

import kotlinx.coroutines.flow.Flow

interface AudioDownloadRepository {

    fun downloadAudio(url: String)

    fun removeDownloadAudio(url: String)

    fun observeDownload(url: String): Flow<AudioDownloadState>

    suspend fun isAudioDownloaded(url: String): Boolean

}