package com.flynid.laska.data.audio

import android.content.Context
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    @OptIn(UnstableApi::class)
    fun downloadMp3(
        url: String
    ) {
        val uri = url.toUri()

        val request = DownloadRequest.Builder(
            url, // contentId
            uri
        )
            .setMimeType(MimeTypes.AUDIO_MPEG)
            .build()

        DownloadService.sendAddDownload(
            context,
            LaskaDownloadService::class.java,
            request,
            false
        )
    }

    @OptIn(UnstableApi::class)
    fun removeDownload(contentId: String) {
        DownloadService.sendRemoveDownload(
            context,
            LaskaDownloadService::class.java,
            contentId,
            false
        )
    }
}